package domain.model.database

import domain.model.{PlayerId, ReplayId}

case class DbRatingHistoryInsert(
    replayId: ReplayId,
    playerId: PlayerId,
    ratingBefore: Double,
    ratingAfter: Double,
    deviationBefore: Double,
    deviationAfter: Double,
    volatilityBefore: Double,
    volatilityAfter: Double
)
