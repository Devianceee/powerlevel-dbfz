package query

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import domain.model.*
import domain.model.Metas.given
import domain.model.database.{PlayerProfileRow, PlayerSearchRow, PlayerTimelineRow}

trait PlayerQueries {
  def findPlayers(name: PlayerName, limit: Int = 20): ConnectionIO[List[PlayerSearchRow]]
  def playerProfile(playerId: PlayerId): ConnectionIO[PlayerProfileRow]
  def playerTimeline(playerId: PlayerId, limit: Int = 100): ConnectionIO[List[PlayerTimelineRow]]
  def winRate(playerId: PlayerId): ConnectionIO[WinRateRow]
  def ratingGraph(playerId: PlayerId): ConnectionIO[List[RatingPoint]]
}

final class DoobiePlayerQueries extends PlayerQueries {
  override def findPlayers(name: PlayerName, limit: Int = 20): ConnectionIO[List[PlayerSearchRow]] =
    sql"""
      SELECT
        p.id,
        p.name,
        r.rating

      FROM player p

      LEFT JOIN rating r
        ON r.player_id = p.id

      WHERE p.name ILIKE ${s"%$name%"}

      ORDER BY
        r.rating DESC NULLS LAST

      LIMIT $limit
    """.query[PlayerSearchRow].to[List]

  override def playerProfile(playerId: PlayerId): ConnectionIO[PlayerProfileRow] =
    sql"""
      SELECT
        p.id,
        p.name,
        r.rating,
        r.deviation

      FROM player p
      LEFT JOIN rating r
        ON r.player_id = p.id
      WHERE p.id = $playerId

    """.query[PlayerProfileRow].unique

  override def playerTimeline(playerId: PlayerId, limit: Int = 100): ConnectionIO[List[PlayerTimelineRow]] =
    sql"""
    SELECT
      rh.replay_id,
      r.played_at,

      CASE
        WHEN r.winner_id = $playerId
        THEN loser.name
        ELSE winner.name
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN loser.id
        ELSE winner.id
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN TRUE
        ELSE FALSE
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.winner_character_1
        ELSE r.loser_character_1
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.winner_character_2
        ELSE r.loser_character_2
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.winner_character_3
        ELSE r.loser_character_3
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.loser_character_1
        ELSE r.winner_character_1
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.loser_character_2
        ELSE r.winner_character_2
      END,

      CASE
        WHEN r.winner_id = $playerId
        THEN r.loser_character_3
        ELSE r.winner_character_3
      END,

      rh.rating_before,
      rh.rating_after

    FROM rating_history rh

    JOIN replay r
      ON r.id = rh.replay_id

    JOIN player winner
      ON winner.id = r.winner_id

    JOIN player loser
      ON loser.id = r.loser_id

    WHERE rh.player_id = $playerId

    ORDER BY r.played_at DESC

    LIMIT $limit
  """
      .query[PlayerTimelineRow]
      .to[List]

  override def winRate(playerId: PlayerId): ConnectionIO[WinRateRow] =
    sql"""
      SELECT
        COUNT(*) FILTER (
          WHERE r.winner_id = ${playerId}
        ) AS wins,

        COUNT(*) FILTER (
          WHERE r.loser_id = ${playerId}
        ) AS losses

      FROM replay r

      WHERE
        r.winner_id = ${playerId}
        OR
        r.loser_id = ${playerId}

    """.query[(Long, Long)].unique.map { case (wins, losses) =>
      val total = wins + losses

      WinRateRow(wins = wins.toInt, losses = losses.toInt, winRate = if (total == 0) 0.0 else (wins.toDouble / total.toDouble) * 100)
    }

  override def ratingGraph(playerId: PlayerId): ConnectionIO[List[RatingPoint]] =
    sql"""
      SELECT
        replay_id,
        rating_after,
        created_at

      FROM rating_history

      WHERE player_id = ${playerId}

      ORDER BY created_at ASC
    """.query[RatingPoint].to[List]
}
