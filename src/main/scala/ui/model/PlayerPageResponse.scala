package ui.model

import domain.model.database.PlayerTimelineRow
import domain.model.{PlayerId, PlayerName, RatingPoint}

case class PlayerPageResponse(
  playerId: PlayerId,
  name: PlayerName,
  rating: Option[Double],
  rd: Option[Double],
  wins: Int,
  losses: Int,
  winRate: Double,
  timeline: List[PlayerTimelineRow],
  ratingGraph: List[RatingPoint]
)
