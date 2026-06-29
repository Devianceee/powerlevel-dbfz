import cats.effect.{IO, Resource}
import config.AppConfig

object App {
  val resource: Resource[IO, Unit] = {

    for {

      config <- AppConfig.resource

      xa <- Database.transactor(config.database)

      client <- ReplayClient.resource(config.dbfz)

      playerRepository =
        DoobiePlayerRepository(xa)

      replayRepository =
        DoobieReplayRepository(xa)

      ratingRepository =
        DoobieRatingRepository(xa)

      leaderboardService =
        LeaderboardService(
          playerRepository,
          ratingRepository
        )

      replayService =
        ReplayService(
          client,
          replayRepository,
          playerRepository,
          ratingRepository
        )

      pollingService =
        PollingService(
          replayService
        )

      routes =
        ApiRoutes(
          leaderboardService,
          replayService
        ) <+>
          UiRoutes(
            leaderboardService,
            replayService
          )

      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(routes.orNotFound)
          .build

      _ <-
        Resource.make(
          pollingService.start
        )(_.cancel)

    } yield ()

  }
}
