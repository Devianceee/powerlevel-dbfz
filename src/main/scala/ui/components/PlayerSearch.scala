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
        id             := "search-field",
        tpe            := "search",
        name           := "q",
        placeholder    := "Search players",
        autocomplete   := "off",
        attr("hx-get") := "/player/search",
        // Added focus condition: Re-fetches results if they click back in, but ONLY if they've typed text
        attr("hx-trigger") := "input changed delay:400ms, keyup[key=='Enter'], focus[this.value.trim()!='']",
        attr("hx-target")  := "#search-results",
        attr("hx-swap")    := "innerHTML",
        attr("onkeydown")  := "if(event.key === 'Escape') { document.getElementById('search-results').innerHTML = ''; this.blur(); }",
        attr("onblur")     := "setTimeout(() => { const res = document.getElementById('search-results'); if(res) res.innerHTML = ''; }, 200);"
      ),
      div(id := "search-results")
    )

  // -------------------------
  // HTMX results fragment
  // -------------------------
  def results(players: List[PlayerSearchResponse]) =
    div(cls := "search-results-card")(
      if (players.isEmpty) div(cls := "search-empty")("No players found") else table(cls := "search-table")(tbody(players.map(row)*))
    )

  // -------------------------
  // Row rendering
  // -------------------------
  def row(p: PlayerSearchResponse) = tr(td(a(href := s"/player/${p.playerId}")(p.name.value)), td(cls := "rating")(p.rating.map(r => f"$r%.0f")))
}
