package repository

import cats.effect.IO
import domain.model.Player
import doobie.Transactor

trait PlayerRepository {
  def upsert(player: Player): IO[Unit]
  def find(player: Player): IO[Option[Player]]
}

final class DoobiePlayerRepository(xa: Transactor[IO]) extends PlayerRepository {
  override def upsert(player: Player): IO[Unit] = ???

  override def find(player: Player): IO[Option[Player]] = ???
}
