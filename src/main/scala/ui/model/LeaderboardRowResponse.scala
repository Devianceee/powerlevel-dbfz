package ui.model

import domain.model.PlayerId
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class LeaderboardRowResponse(playerId: PlayerId, name: String, rating: Int, rd: Double, volatility: Double)