package ui.model

import domain.model.{PlayerName, PlayerId}
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class PlayerSearchResponse(playerId: PlayerId, name: PlayerName, rating: Option[Double])
