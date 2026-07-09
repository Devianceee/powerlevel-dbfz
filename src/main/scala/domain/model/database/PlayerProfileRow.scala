package domain.model.database

import domain.model.{PlayerId, PlayerName}

final case class PlayerProfileRow(playerId: PlayerId, name: PlayerName, rating: Option[Double], deviation: Option[Double])
