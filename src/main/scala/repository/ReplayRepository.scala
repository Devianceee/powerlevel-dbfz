package repository

import cats.data.NonEmptyList
import cats.effect.IO
import domain.model.{Replay, ReplayId}
import doobie.Transactor

import scala.annotation.unused

trait ReplayRepository {
  def insert(replay: Replay): IO[Unit]
  def insertMany(replay: NonEmptyList[Replay]): IO[Unit]
  def exists(replayId: ReplayId): IO[Boolean]
  def latest(limit: Int): IO[List[Replay]]
}

final class DoobieReplayRepository(@unused xa: Transactor[IO]) extends ReplayRepository {
  override def insert(replay: Replay): IO[Unit] = ???

  override def insertMany(replay: NonEmptyList[Replay]): IO[Unit] = ???

  override def exists(replayId: ReplayId): IO[Boolean] = ???

  override def latest(limit: Int): IO[List[Replay]] = ???

}
