package domain.model

opaque type AuthToken = String

object AuthToken {
  def apply(value: String): AuthToken = value

  extension (value: AuthToken) {
    def value: String = value
  }
}
