package ui.model

import domain.model.PlayerId
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class PlayerSearchResponse (playerId: PlayerId, name: String, rating: Option[Double])
