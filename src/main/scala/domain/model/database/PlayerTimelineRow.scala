package domain.model.database

import domain.model.{PlayerId, ReplayId}

import java.time.OffsetDateTime

final case class PlayerTimelineRow(
    replayId: ReplayId,
    playedAt: OffsetDateTime,
    opponentName: String,
    opponentId: PlayerId,
    isWin: Boolean,
    ratingBefore: Double,
    ratingAfter: Double
)
