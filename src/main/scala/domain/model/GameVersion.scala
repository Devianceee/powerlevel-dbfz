package domain.model

opaque type GameVersion = Int

object GameVersion {
  def apply(value: Int): GameVersion = value

  extension (id: GameVersion) {
    def value: Int = id
  }
}
