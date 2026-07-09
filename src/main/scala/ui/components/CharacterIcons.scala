package ui.components

import domain.enums.DbfzCharacter
import domain.model.MatchCharacters
import scalatags.Text.all.*

object CharacterIcons {

  def view(characters: MatchCharacters) =
    div(cls := "character-icons")(
      character(characters.first),
      character(characters.second),
      character(characters.third)
    )

  private def character(character: DbfzCharacter) =
    img(
      cls := "character-icon",
      src := character.imagePath,
      alt := character.displayName,
      title := character.displayName
    )
}