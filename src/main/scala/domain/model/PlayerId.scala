package domain.model

import doobie.Meta
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveEncoder

import scala.annotation.nowarn

opaque type PlayerId = Long

object PlayerId {
  def apply(value: Long): PlayerId = value

  extension (id: PlayerId) {
    def value: Long = id
  }
}
    

