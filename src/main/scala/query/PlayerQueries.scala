package query

import cats.effect.IO
import doobie.*
//import doobie.implicits.*

import java.time.Instant

import domain.model.*

trait PlayerQueries {
  def playerTimeline(playerId: PlayerId, limit: Int = 100): IO[List[PlayerTimelineRow]]
  def winRate(playerId: PlayerId): IO[WinRateRow]
  def ratingGraph(playerId: PlayerId): IO[List[RatingPoint]]
}

final class DoobiePlayerQueries(xa: Transactor[IO]) extends PlayerQueries {
  override def ratingGraph(playerId: PlayerId): IO[List[RatingPoint]]                      = ???
  override def playerTimeline(playerId: PlayerId, limit: Int): IO[List[PlayerTimelineRow]] = ???
  override def winRate(playerId: PlayerId): IO[WinRateRow]                                 = ???
}

final case class PlayerTimelineRow(
    replayId: ReplayId,
    playedAt: Instant,
    opponentName: String,
    isWin: Boolean,
    ratingBefore: Int,
    ratingAfter: Int
)

final case class WinRateRow(
    wins: Int,
    losses: Int,
    winRate: Double
)

final case class RatingPoint(
    replayId: ReplayId,
    rating: Int,
    timestamp: Instant
)
