package query

import domain.model.Metas.given
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
        r.deviation

      FROM rating r
  
      JOIN player p
        ON p.id = r.player_id

      WHERE r.deviation < 75 

      ORDER BY r.rating DESC

      LIMIT $limit
    """.query[DbLeaderboardRow].to[List]
}
