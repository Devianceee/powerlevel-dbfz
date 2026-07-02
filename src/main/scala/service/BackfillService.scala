package service

import cats.effect.IO
import client.ReplayClient

trait BackfillService {
  def run: IO[Unit]
}

final class BackfillServiceImpl(
    client: ReplayClient,
    ingestionService: ReplayIngestionService
) extends BackfillService {
  override def run: IO[Unit] = ???
}
