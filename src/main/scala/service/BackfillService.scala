package service

import cats.effect.IO
import client.ReplayClient

trait BackfillService {
  def run: IO[Unit]
}

final class BackfillServiceImpl(ingestionService: RatingService) extends BackfillService {
  override def run: IO[Unit] = ???
}
