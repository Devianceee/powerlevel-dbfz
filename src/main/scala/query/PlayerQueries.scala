package query

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*

import java.time.OffsetDateTime
import domain.model.*
import domain.model.Metas.playerIdMeta
import domain.model.database.{PlayerProfileRow, PlayerSearchRow, PlayerTimelineRow}

trait PlayerQueries {
  def findPlayers(name: String, limit: Int = 20): IO[List[PlayerSearchRow]]
  def playerProfile(playerId: PlayerId): IO[PlayerProfileRow]
  def playerTimeline(playerId: PlayerId, limit: Int = 100): IO[List[PlayerTimelineRow]]
  def winRate(playerId: PlayerId): IO[WinRateRow]
  def ratingGraph(playerId: PlayerId): IO[List[RatingPoint]]
}

final class DoobiePlayerQueries(xa: Transactor[IO]) extends PlayerQueries {
  override def findPlayers(
      name: String,
      limit: Int = 20
  ): IO[List[PlayerSearchRow]] =
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
    """
      .query[PlayerSearchRow]
      .to[List]
      .transact(xa)

  override def playerProfile(playerId: PlayerId): IO[PlayerProfileRow] =
    sql"""
         SELECT
           p.id,
           p.name,
   
           r.rating,
           r.deviation,
           r.volatility,
   
           COUNT(*) FILTER (
             WHERE replay.winner_id = p.id
           ) AS wins,
   
           COUNT(*) FILTER (
             WHERE replay.loser_id = p.id
           ) AS losses,
   
           (
             COUNT(*) FILTER (
               WHERE replay.winner_id = p.id
             )::double precision
             /
             NULLIF(COUNT(*), 0)
             * 100
           ) AS win_rate
   
         FROM player p
   
         JOIN rating r
           ON r.player_id = p.id
   
         LEFT JOIN rating_history rh
           ON rh.player_id = p.id
   
         LEFT JOIN replay
           ON replay.id = rh.replay_id
   
         WHERE p.id = ${playerId.value}
   
         GROUP BY
           p.id,
           p.name,
           r.rating,
           r.deviation,
           r.volatility
       """
      .query[PlayerProfileRow]
      .unique
      .transact(xa)

  override def playerTimeline(
      playerId: PlayerId,
      limit: Int = 100
  ): IO[List[PlayerTimelineRow]] =
    sql"""
       SELECT
         rh.replay_id,
         r.played_at,
 
         CASE
           WHEN r.winner_id = ${playerId.value}
           THEN loser.name
           ELSE winner.name
         END AS opponent_name,
         
         CASE
           WHEN r.winner_id = ${playerId.value}
           THEN loser.name
           ELSE winner.name
         END AS opponent_id,
 
         CASE
           WHEN r.winner_id = ${playerId.value}
           THEN TRUE
           ELSE FALSE
         END AS is_win,
 
         rh.rating_before,
         rh.rating_after
 
       FROM rating_history rh
 
       JOIN replay r
         ON r.id = rh.replay_id
 
       JOIN player winner
         ON winner.id = r.winner_id
 
       JOIN player loser
         ON loser.id = r.loser_id
 
       WHERE rh.player_id = ${playerId.value}
 
       ORDER BY r.played_at DESC
 
       LIMIT $limit
     """
      .query[PlayerTimelineRow]
      .to[List]
      .transact(xa)

  override def winRate(
      playerId: PlayerId
  ): IO[WinRateRow] =
    sql"""
       SELECT
         COUNT(*) FILTER (
           WHERE r.winner_id = ${playerId.value}
         ) AS wins,
 
         COUNT(*) FILTER (
           WHERE r.loser_id = ${playerId.value}
         ) AS losses
 
       FROM replay r
 
       WHERE
         r.winner_id = ${playerId.value}
         OR
         r.loser_id = ${playerId.value}
     """
      .query[(Long, Long)]
      .unique
      .map { case (wins, losses) =>
        val total = wins + losses

        WinRateRow(
          wins = wins.toInt,
          losses = losses.toInt,
          winRate =
            if (total == 0) 0.0
            else wins.toDouble / total.toDouble
        )
      }
      .transact(xa)

  override def ratingGraph(
      playerId: PlayerId
  ): IO[List[RatingPoint]] =
    sql"""
       SELECT
         replay_id,
         rating_after,
         created_at
 
       FROM rating_history
 
       WHERE player_id = ${playerId.value}
 
       ORDER BY created_at ASC
     """
      .query[RatingPoint]
      .to[List]
      .transact(xa)
}
