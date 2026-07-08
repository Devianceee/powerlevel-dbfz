package domain.model.database

import domain.model.{PlayerId, PlayerName}

final case class PlayerSearchRow(playerId: PlayerId, name: PlayerName, rating: Option[Double])
