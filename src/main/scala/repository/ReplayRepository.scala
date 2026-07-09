package repository

import domain.model.database.DbReplayRow
import domain.model.ReplayId
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.ConnectionIO

trait ReplayRepository {
  def insert(replay: DbReplayRow): ConnectionIO[Int]
  def exists(replayId: ReplayId): ConnectionIO[Boolean]
}

final class DoobieReplayRepository extends ReplayRepository {
  override def insert(replay: DbReplayRow): ConnectionIO[Int] =
    sql"""
    INSERT INTO replay (
      id,

      winner_id,
      loser_id,

      winner_character_1,
      winner_character_2,
      winner_character_3,

      loser_character_1,
      loser_character_2,
      loser_character_3,

      played_at
    )
    VALUES (
      ${replay.id.value},

      ${replay.winnerId.value},
      ${replay.loserId.value},

      ${replay.winnerCharacters.first.id},
      ${replay.winnerCharacters.second.id},
      ${replay.winnerCharacters.third.id},

      ${replay.loserCharacters.first.id},
      ${replay.loserCharacters.second.id},
      ${replay.loserCharacters.third.id},

      ${replay.playedAt}
    )
    ON CONFLICT (id)
    DO NOTHING
  """.update.run

  override def exists(replayId: ReplayId): ConnectionIO[Boolean] =
    sql"""
          SELECT EXISTS(
            SELECT 1
            FROM replay
            WHERE id = ${replayId.value}
          )
        """.query[Boolean].unique

}
