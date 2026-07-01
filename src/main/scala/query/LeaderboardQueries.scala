package query

import cats.effect.IO
import domain.model.PlayerId
import doobie.*
import doobie.implicits.*

import java.time.Instant

trait LeaderboardQueries {
  def globalLeaderboard(limit: Int): IO[List[LeaderboardRow]]
}

final class DoobieLeaderboardQueries(xa: Transactor[IO]) extends LeaderboardQueries {
  override def globalLeaderboard(limit: Int): IO[List[LeaderboardRow]] =
    sql"""
      SELECT
        p.player_id,
        p.name,
        r.rating,
        r.rd,
        r.volatility
      FROM rating r
      JOIN player p ON p.player_id = r.player_id
      ORDER BY r.rating DESC
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