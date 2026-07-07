package ui.model

import domain.model.database.PlayerTimelineRow
import domain.model.{PlayerId, RatingPoint}

case class PlayerPageResponse(
    playerId: PlayerId,
    name: String,
    rating: Option[Double],
    rd: Option[Double],
    volatility: Option[Double],
    wins: Int,
    losses: Int,
    winRate: Double,
    timeline: List[PlayerTimelineRow],
    ratingGraph: List[RatingPoint]
)
