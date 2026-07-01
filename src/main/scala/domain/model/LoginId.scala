package domain.model

opaque type LoginId = Long

object LoginId {
  def apply(value: Long): LoginId = value

  extension (id: LoginId) {
    def value: Long = id
  }
}
