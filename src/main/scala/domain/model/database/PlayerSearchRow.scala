package domain.model.database

import domain.model.PlayerId

final case class PlayerSearchRow(
    playerId: PlayerId,
    name: String,
    rating: Option[Double]
)
