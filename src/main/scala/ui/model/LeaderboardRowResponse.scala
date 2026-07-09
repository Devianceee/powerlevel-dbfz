package ui.model

import domain.model.{PlayerId, PlayerName}
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class LeaderboardRowResponse(playerId: PlayerId, name: PlayerName, rating: Double, rd: Double)
