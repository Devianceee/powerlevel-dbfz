package service

import cats.effect.IO
import client.ReplayClient

trait BackfillService {
  def run: IO[Unit]
}

final class BackfillServiceImpl(ratingService: IngestService) extends BackfillService {
  override def run: IO[Unit] = ratingService.ingest(5000)
}
