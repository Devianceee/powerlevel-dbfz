package domain.model

import doobie.Meta
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveEncoder

import scala.annotation.nowarn

opaque type PlayerName = String

object PlayerName {
  def apply(value: String): PlayerName = value

  extension (id: PlayerName) { def value: String = id }
}
