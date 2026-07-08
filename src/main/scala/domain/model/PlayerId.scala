package domain.model

opaque type PlayerId = Long

object PlayerId {
  def apply(value: Long): PlayerId = value

  extension (id: PlayerId) { def value: Long = id }
}
