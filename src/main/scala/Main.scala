import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.*
import config.Config

object Main extends IOApp {
  def run(config: Config, args: List[String]): IO[ExitCode] =
    App.resource(config).use { app =>
      args match {
        case "--backfill" :: Nil =>
          for {
            _ <- IO.println("Starting backfill...")
            _ <- app.backfill.run
            _ <- IO.println("Backfill complete.")
            x <- App.runServer(config, app)
          } yield x

        case _ =>
          App.runServer(config, app)
      }
    }
}
