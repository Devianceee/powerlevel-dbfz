import cats.MonoidK.ops.toAllMonoidKOps
import cats.effect.kernel.Outcome
import cats.effect.{ExitCode, IO, Resource}
import client.{HttpLoginClient, HttpReplayClient, LoginClient, ReplayClient}
import config.{Config, DbfzConfig}
import database.{Database, FlywayMigration}
import org.http4s.HttpApp
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import repository.*
import routes.*
import service.*
import com.comcast.ip4s.*
import domain.model.{AuthToken, PlayerId}
import org.http4s.ember.server.EmberServerBuilder
import query.*
import rating.GlickoCalculator
import scheduler.*

import scala.concurrent.duration.DurationInt

object App {
  final case class Application(
      httpApp: HttpApp[IO],
      poller: ReplayPolling,
      backfill: BackfillService
  )

  def resource(config: Config): Resource[IO, Application] =
    for {
      // -- Setup database and repositories --
      _  <- Resource.eval(FlywayMigration.migrate(config.database))
      xa <- Database.resource(config.database)

      playerRepository        = DoobiePlayerRepository(xa)
      replayRepository        = DoobieReplayRepository(xa)
      ratingRepository        = DoobieRatingRepository(xa)
      ratingHistoryRepository = DoobieRatingHistoryRepository(xa)

      calculator = GlickoCalculator()
      replayClient <- replayClientResource(config)

      // -- Setup WRITE services + scheduler (isn't used by UI + API) --
      replayIngestionService =
        ReplayIngestionServiceImpl(
          replayClient,
          playerRepository,
          replayRepository,
          ratingRepository,
          ratingHistoryRepository,
          calculator
        )

      backfillService = BackfillServiceImpl(replayIngestionService)
      pollingService  = ReplayPollingImpl(replayIngestionService)

      // -- Setup READ services + routes (used by UI + API) --
      leaderboardQueries = DoobieLeaderboardQueries(xa)
      playerQueries      = DoobiePlayerQueries(xa)

      leaderboardService = LeaderboardServiceImpl(leaderboardQueries)
      playerService      = PlayerServiceImpl(playerQueries)

      apiRoutes    = ApiRoutes(leaderboardService, playerService).routes
      uiRoutes     = UiRoutes(leaderboardService, playerService).routes
      healthRoutes = HealthRoutes.routes

      httpApp = (healthRoutes <+> uiRoutes <+> apiRoutes).orNotFound

    } yield Application(
      httpApp = httpApp,
      poller = pollingService,
      backfill = backfillService
    )

  def runServer(
      config: Config,
      app: Application
  ): IO[ExitCode] =
    for {
      _ <- IO.println("Starting poller...")
      _ <- app.poller.start.guaranteeCase {
        case Outcome.Succeeded(_) => IO.println("Poller finished")
        case Outcome.Errored(e)   => IO.println(s"Poller failed: ${e.getMessage}")
        case Outcome.Canceled()   => IO.println("Poller cancelled")
      }.start

      _ <- IO.println("Starting HTTP server...")
      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(config.server.port)
          .withHttpApp(app.httpApp)
          .build
          .useForever

    } yield ExitCode.Success

  private def httpClient: Resource[IO, Client[IO]] =
    EmberClientBuilder
      .default[IO]
      .withTimeout(60.seconds)
      .withIdleConnectionTime(60.seconds)
      .build

  private def loginClient(
      client: Client[IO],
      config: DbfzConfig
  ): LoginClient =
    HttpLoginClient(client, config)

  private def replayClient(
      client: Client[IO],
      token: AuthToken,
      playerId: PlayerId
  ): ReplayClient =
    HttpReplayClient(client, token, playerId)

  private def replayClientResource(config: Config): Resource[IO, ReplayClient] =
    for {
      client <- httpClient
      login = loginClient(client, config.dbfzConfig)
      loginResp <- Resource.eval(login.login)
      replay = replayClient(client, loginResp.authToken, loginResp.playerId)
    } yield replay
}
