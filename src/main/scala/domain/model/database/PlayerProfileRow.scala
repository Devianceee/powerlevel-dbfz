package domain.model.database

import domain.model.PlayerId

final case class PlayerProfileRow(
    playerId: PlayerId,
    name: String,
    rating: Option[Double],
    deviation: Option[Double],
    volatility: Option[Double]
)
