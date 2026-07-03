package domain.model

import ciris.ConfigDecoder

opaque type SteamId = Long

object SteamId {
  def apply(value: Long): SteamId = value

  extension (id: SteamId) {
    def value: Long = id
  }

  given ConfigDecoder[String, SteamId] =
    ConfigDecoder[String, Long].map(SteamId.apply)
}
