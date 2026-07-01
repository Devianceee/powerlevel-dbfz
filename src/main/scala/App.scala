import cats.effect.{IO, Resource}
import client.{AuthToken, HttpLoginClient, HttpReplayClient, LoginClient, ReplayClient}
import config.{Config, DbfzConfig}
import database.{Database, FlywayMigration}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import repository.{DoobiePlayerRepository, DoobieRatingRepository, DoobieReplayRepository}
import routes.{ApiRoutes, UiRoutes}
import service.{LeaderboardService, ReplayService, ReplayServiceImpl}
import com.comcast.ip4s.*
import scheduler.ReplayPolling

import scala.concurrent.duration.DurationInt

object App {
  val resource: Resource[IO, Unit] =
    for {
      config <- Config.read
      _      <- Resource.eval(FlywayMigration.migrate(config.database))
      xa     <- Database.resource(config.database)

      playerRepository = DoobiePlayerRepository(xa)
      replayRepository = DoobieReplayRepository(xa)
      ratingRepository = DoobieRatingRepository(xa)

      replayClient <- replayClientResource(config)

      replayService  = ReplayServiceImpl(replayClient, replayRepository, playerRepository, ratingRepository)
      pollingService = ReplayPollingImpl(replayService)

      leaderboardService = LeaderboardService(playerRepository, ratingRepository)

      apiRoutes = ApiRoutes(leaderboardService)
      uiRoutes  = UiRoutes(leaderboardService)

      routes = uiRoutes <+> apiRoutes

      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(config.server.port)
          .withHttpApp(routes.orNotFound)
          .build

      _ <- Resource.make(pollingService.start)(_.cancel)
    } yield ()

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
      token: AuthToken
  ): ReplayClient =
    HttpReplayClient(client, token)

  private def replayClientResource(config: Config): Resource[IO, ReplayClient] =
    for {
      client <- httpClient
      login = loginClient(client, config.dbfzConfig)
      token <- Resource.eval(login.login)
      replay = replayClient(client, token)
    } yield replay
}
