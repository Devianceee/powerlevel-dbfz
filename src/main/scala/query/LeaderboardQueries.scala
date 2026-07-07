package query

import cats.effect.IO
import domain.model.Metas.playerIdMeta
import domain.model.PlayerId
import doobie.*
import doobie.implicits.*

import java.time.Instant

trait LeaderboardQueries {
  def globalLeaderboard(limit: Int = 100): IO[List[LeaderboardRow]]
}

final class DoobieLeaderboardQueries(xa: Transactor[IO]) extends LeaderboardQueries {
  override def globalLeaderboard(limit: Int = 100): IO[List[LeaderboardRow]] =
    sql"""
      SELECT
        p.id,
        p.name,

        r.rating,
        r.deviation,
        r.volatility,

        (r.rating - 2 * r.deviation) AS conservative_rating,

        COUNT(rh.id) AS games_played

      FROM rating r

      JOIN player p
        ON p.id = r.player_id

      JOIN rating_history rh
        ON rh.player_id = p.id

      GROUP BY
        p.id,
        p.name,
        r.rating,
        r.deviation,
        r.volatility

      HAVING COUNT(rh.id) >= 10

      ORDER BY conservative_rating DESC

      LIMIT $limit
    """
      .query[LeaderboardRow]
      .to[List]
      .transact(xa)
}

final case class LeaderboardRow(
    playerId: PlayerId,
    name: String,
    rating: Int,
    rd: Double,
    volatility: Double
)
