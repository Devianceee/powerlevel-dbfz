package repository

import domain.model.database.DbPlayerRow
import domain.model.{Player, PlayerId, PlayerName}
import doobie.ConnectionIO
import doobie.syntax.string.toSqlInterpolator

trait PlayerRepository {
  def find(player: Player): ConnectionIO[Option[Player]]
  def upsert(player: DbPlayerRow): ConnectionIO[Int]
}

final class DoobiePlayerRepository extends PlayerRepository {
  override def find(player: Player): ConnectionIO[Option[Player]] =
    sql"""
          SELECT id, name
          FROM player
          WHERE id = ${player.playerId.value}
        """.query[(Long, String)].option.map(_.map { case (id, name) => Player(playerId = PlayerId(id), username = PlayerName(name)) })

  override def upsert(player: DbPlayerRow): ConnectionIO[Int] =
    sql"""
          INSERT INTO player (
            id,
            name
          )
          VALUES (
            ${player.playerId.value},
            ${player.username.value}
          )
          ON CONFLICT (id)
          DO UPDATE SET
            name = EXCLUDED.name,
            updated_at = NOW()
        """.update.run
}
