package domain.model.database

import domain.model.{PlayerId, PlayerName}

final case class DbLeaderboardRow(playerId: PlayerId, name: PlayerName, rating: Double, rd: Double, volatility: Double)
