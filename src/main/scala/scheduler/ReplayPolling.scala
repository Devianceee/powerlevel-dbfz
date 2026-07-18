package scheduler

import cats.effect.IO
import config.PollingConfig
import service.IngestService

import scala.concurrent.duration.DurationInt

trait ReplayPolling {
  def start: IO[Unit]
}

final class ReplayPollingImpl(config: PollingConfig, ratingService: IngestService) extends ReplayPolling {
  override def start: IO[Unit] = loop.foreverM

  private def loop: IO[Unit] =
    for {
      _ <- IO.println("Polling and ingesting latest replays...")
      _ <- ratingService.ingest(limit = 999).handleErrorWith { e =>
        IO.println(s"Poller failed: ${e.getMessage}")
      }
      _ <- IO.println(s"Sleeping for ${config.pollingInterval.seconds} seconds.")
      _ <- IO.sleep(config.pollingInterval.seconds)
    } yield ()
}
