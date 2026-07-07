package ui.components

import scalatags.Text.all.*
import ui.model.PlayerSearchResponse

object PlayerSearch {

  // -------------------------
  // Search input
  // -------------------------
  def view =
    div(cls := "player-search")(
      input(
        id           := "search-field",
        tpe          := "search",
        name         := "q",
        placeholder  := "Search players",
        autocomplete := "off",

        attr("hx-get")     := "/player/search",
        attr("hx-trigger") := "input changed delay:400ms, keyup[key=='Enter']",
        attr("hx-target")  := "#search-results",
        attr("hx-swap")    := "innerHTML"
      ),

      div(id := "search-results")
    )

  // -------------------------
  // HTMX results fragment
  // -------------------------
  def results(players: List[PlayerSearchResponse]) =
    div(cls := "search-results-card")(
      if (players.isEmpty)
        div(cls := "search-empty")("No players found")
      else
        table(cls := "search-table")(
          tbody(
            players.map(row)*
          )
        )
    )

  // -------------------------
  // Row rendering
  // -------------------------
  def row(p: PlayerSearchResponse) =
    tr(
      td(
        a(
          href := s"/player/${p.playerId}"
        )(
          p.name
        )
      ),

      td(cls := "rating")(
        f"${p.rating}"
      )
    )
}
