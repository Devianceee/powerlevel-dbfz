package repository

import cats.effect.IO
import domain.model.Player
import doobie.{ConnectionIO, Transactor}

import scala.annotation.unused
//import doobie.syntax.string.toSqlInterpolator

trait PlayerRepository {
  def upsert(player: Player): ConnectionIO[Unit]
  def find(player: Player): IO[Option[Player]]
}

final class DoobiePlayerRepository(@unused xa: Transactor[IO]) extends PlayerRepository {
  override def upsert(player: Player): ConnectionIO[Unit] = ???
  override def find(player: Player): IO[Option[Player]]   = ???
}
