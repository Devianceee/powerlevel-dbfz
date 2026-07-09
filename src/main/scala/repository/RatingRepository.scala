package repository

import cats.effect.IO
import domain.model.Metas.given
import domain.model.database.DbRatingRow
import domain.model.{Player, PlayerId, Rating}
import doobie.implicits.*
import doobie.{ConnectionIO, Transactor}

trait RatingRepository {
  def find(playerId: PlayerId): ConnectionIO[Option[DbRatingRow]]
  def upsert(rating: DbRatingRow): ConnectionIO[Int]

}

final class DoobieRatingRepository extends RatingRepository {
  override def find(playerId: PlayerId): ConnectionIO[Option[DbRatingRow]] =
    sql"""
          SELECT
            player_id,
            rating,
            deviation,
            volatility
          FROM rating
          WHERE player_id = ${playerId.value}
        """.query[DbRatingRow].option

  override def upsert(rating: DbRatingRow): ConnectionIO[Int] =
    sql"""
          INSERT INTO rating (
            player_id,
            rating,
            deviation,
            volatility
          )
          VALUES (
            ${rating.playerId.value},
            ${rating.rating},
            ${rating.deviation},
            ${rating.volatility}
          )
          ON CONFLICT (player_id)
          DO UPDATE SET
            rating = EXCLUDED.rating,
            deviation = EXCLUDED.deviation,
            volatility = EXCLUDED.volatility,
            updated_at = NOW()
        """.update.run
}
