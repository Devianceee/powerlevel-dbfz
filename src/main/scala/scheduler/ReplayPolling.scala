package scheduler

import cats.effect.IO
import client.ReplayClient
import service.ReplayIngestionService

import scala.concurrent.duration.DurationInt

trait ReplayPolling {
  def start: IO[Unit]
}

final class ReplayPollingImpl(ingestionService: ReplayIngestionService) extends ReplayPolling {
  override def start: IO[Unit] =
    (for {
      _ <- IO.println("Polling latest replays...")
//      _ <- replayIngestionService.ingestLatest(limit = 100)
      _ <- IO.sleep(60.seconds)
    } yield ()).foreverM
}
