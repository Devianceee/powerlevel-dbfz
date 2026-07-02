import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s._

import config.Config

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Config.read.load[IO].flatMap { config =>
      App.resource(config).use { app =>
        args match {
          case "--backfill" :: Nil =>
            for {
              _ <- IO.println("Starting backfill...")
              _ <- app.backfill.run
              _ <- IO.println("Backfill complete.")
            } yield ExitCode.Success

          case _ =>
            for {
              _ <- IO.println("Starting poller...")

              // start polling in background
              pollerFiber <- app.poller.start.start

              _ <- IO.println("Starting HTTP server...")

              _ <-
                org.http4s.ember.server.EmberServerBuilder
                  .default[IO]
                  .withHost(ipv4"0.0.0.0")
                  .withPort(config.server.port)
                  .withHttpApp(app.httpApp)
                  .build
                  .useForever

              _ <- pollerFiber.cancel
            } yield ExitCode.Success
        }
      }
    }
}