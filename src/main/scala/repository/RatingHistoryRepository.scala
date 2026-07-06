package repository

import cats.effect.IO
import domain.model.Rating
import doobie.Transactor

import scala.annotation.unused

trait RatingHistoryRepository {
  def insert(rating: Rating): IO[Unit]
  def history(limit: Int): IO[List[Rating]]
}

final class DoobieRatingHistoryRepository(@unused xa: Transactor[IO]) extends RatingHistoryRepository {
  override def insert(rating: Rating): IO[Unit] = ???

  override def history(limit: Int): IO[List[Rating]] = ???
}
