package scheduler

import cats.effect.IO
import config.PollingConfig
import service.IngestService

import scala.concurrent.duration.DurationInt

trait ReplayPolling {
  def start: IO[Unit]
}

final class ReplayPollingImpl(config: PollingConfig, ratingService: IngestService) extends ReplayPolling {
  override def start: IO[Unit] =
    (for {
      _ <- IO.println("Polling and ingesting latest replays...")
      _ <- ratingService.ingest(limit = 100)
      _ <- IO.println(s"Finished, sleeping for ${config.pollingInterval.seconds} seconds.")
      _ <- IO.sleep(config.pollingInterval.seconds)
    } yield ()).foreverM
}
