package domain.model.database

import domain.model.{PlayerId, PlayerName}

case class DbPlayerRow(playerId: PlayerId, username: PlayerName)
