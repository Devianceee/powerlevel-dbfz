package ui.data

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.generic.semiauto.*

import ui.model.SiteUpdate

import scala.io.Source

object Updates {
  given Decoder[SiteUpdate] = deriveDecoder[SiteUpdate]

  def load: IO[List[SiteUpdate]] =
    IO {
      val json =
        Source
          .fromResource("updates.json")
          .mkString

      decode[List[SiteUpdate]](json)
        .getOrElse(List.empty)
    }
}
