package query

import domain.model.Metas.given
import domain.model.database.LatestReplayRow
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

trait ReplaysQueries {
  def latest(limit: Int = 100): ConnectionIO[List[LatestReplayRow]]
}

final class DoobieReplaysQueries extends ReplaysQueries {
  override def latest(limit: Int = 100): ConnectionIO[List[LatestReplayRow]] =
    sql"""
      SELECT
        r.id,
        r.created_at,

        winner.id,
        winner.name,
        loser.id,
        loser.name,

        r.winner_character_1,
        r.winner_character_2,
        r.winner_character_3,

        r.loser_character_1,
        r.loser_character_2,
        r.loser_character_3

      FROM replay r

      JOIN player winner
        ON winner.id = r.winner_id

      JOIN player loser
        ON loser.id = r.loser_id

      ORDER BY r.created_at DESC

      LIMIT $limit
    """
      .query[LatestReplayRow]
      .to[List]
}
