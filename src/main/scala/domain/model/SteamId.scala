package domain.model

opaque type SteamId = Long

object SteamId {
  def apply(value: Long): SteamId = value

  extension (id: SteamId) { def value: Long = id }
}
