import cats.effect.{ExitCode, IO, IOApp}
import config.Config

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- Config.read
      exitCode <- App.resource(config).use { app =>
        args match {
          case "--backfill" :: Nil =>
            for {
              _    <- IO.println("Starting backfill...")
              _    <- app.backfill.run
              _    <- IO.println("Backfill complete.")
              code <- App.runServer(config, app)
            } yield code

          case _ => App.runServer(config, app)
        }
      }
    } yield exitCode
}
