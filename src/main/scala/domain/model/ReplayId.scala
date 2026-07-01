package domain.model

opaque type ReplayId = Long

object ReplayId {
  def apply(value: Long): ReplayId = value

  extension (id: ReplayId) {
    def value: Long = id
  }
}
