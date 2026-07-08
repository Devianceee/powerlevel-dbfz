package query

import domain.model.Metas.{playerIdMeta, playerNameMeta}
import domain.model.database.DbLeaderboardRow
import doobie.*
import doobie.implicits.*

trait LeaderboardQueries {
  def globalLeaderboard(limit: Int = 100): ConnectionIO[List[DbLeaderboardRow]]
}

final class DoobieLeaderboardQueries extends LeaderboardQueries {
  override def globalLeaderboard(limit: Int = 100): ConnectionIO[List[DbLeaderboardRow]] =
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
    """.query[DbLeaderboardRow].to[List]
}
