package domain.model.database

import domain.model.{PlayerId, PlayerName, ReplayId}

import java.time.OffsetDateTime

final case class PlayerTimelineRow(
  replayId: ReplayId,
  playedAt: OffsetDateTime,
  opponentName: PlayerName,
  opponentId: PlayerId,
  isWin: Boolean,
  ratingBefore: Double,
  ratingAfter: Double
)
