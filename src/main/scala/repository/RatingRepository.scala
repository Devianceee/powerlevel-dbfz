package repository

import cats.effect.IO
import domain.model.{PlayerId, Rating}
import doobie.Transactor

import scala.annotation.unused

trait RatingRepository {
  def get(playerId: PlayerId): IO[Option[Rating]]
  def upsert(rating: Rating): IO[Unit]
  def leaderboard(limit: Int): IO[List[Rating]]
}

final class DoobieRatingRepository(@unused xa: Transactor[IO]) extends RatingRepository {
  override def get(playerId: PlayerId): IO[Option[Rating]] = ???

  override def upsert(rating: Rating): IO[Unit] = ???

  override def leaderboard(limit: Int): IO[List[Rating]] = ???
}
