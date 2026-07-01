package repository

import cats.effect.IO
import domain.model.{PlayerId, Rating}

trait RatingHistoryRepository {
  def insert(rating: Rating): IO[Unit]
  def history(limit: Int): IO[List[Rating]]
}

final class DoobieRatingHistoryRepository extends RatingHistoryRepository {
  override def insert(rating: Rating): IO[Unit] = ???

  override def history(limit: Int): IO[List[Rating]] = ???
}