package domain.model

import domain.enums.DbfzCharacter

case class MatchCharacters(first: DbfzCharacter, second: DbfzCharacter, third: DbfzCharacter) {
  def characters: List[DbfzCharacter] = List(first, second, third)
}

object MatchCharacters {
  def fromIds(ids: Seq[Int]): MatchCharacters =
    ids match {
      case Seq(first, second, third) =>
        MatchCharacters(
          DbfzCharacter
            .fromId(first)
            .getOrElse(throw new IllegalArgumentException(s"Unknown character id: $first")),
          DbfzCharacter
            .fromId(second)
            .getOrElse(throw new IllegalArgumentException(s"Unknown character id: $second")),
          DbfzCharacter
            .fromId(third)
            .getOrElse(throw new IllegalArgumentException(s"Unknown character id: $third"))
        )
    }

  def insertCharacters(seq: Seq[DbfzCharacter]): MatchCharacters =
    seq match {
      case Seq(first, second, third) =>
        MatchCharacters(first, second, third)

      case _ =>
        throw new IllegalArgumentException(
          "Expected exactly 3 characters"
        )
    }
}
