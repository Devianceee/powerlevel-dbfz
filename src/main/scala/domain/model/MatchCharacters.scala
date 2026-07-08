package domain.model

case class MatchCharacters(first: Short, second: Short, third: Short)

object MatchCharacters {
  def insertCharacters(seq: Seq[Int]): MatchCharacters =
    seq match {
      case Seq(first, second, third) => MatchCharacters(first.toShort, second.toShort, third.toShort)
      case _                         => throw new IllegalArgumentException("Expected exactly 3 characters")
    }
}
