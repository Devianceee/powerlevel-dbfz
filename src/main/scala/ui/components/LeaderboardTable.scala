package ui.components

import scalatags.Text.all.*
import ui.model.LeaderboardRowResponse

object LeaderboardTable {

  def row(rank: Int, entry: LeaderboardRowResponse) =
    tr(
      td(cls := "rank")(rank.toString),
      td(a(href := s"/player/${entry.playerId}")(entry.name.value)),
      td(cls := "rating-cell")(
        // Main rating rounded to an integer string
        span(cls := "rating-main")(f"${entry.rating}%.0f"),
        // Muted deviation sitting cleanly right next to it
        r => span(cls := "rating-deviation")(f" ±${entry.rd}%.0f")
      ),
      // Volatility tucked into its own low-priority column
      td(cls := "volatility-cell")(
        f"${entry.volatility}%.3f"
      )
    )

  def view(players: List[LeaderboardRowResponse]) =
    table(cls := "leaderboard-table")(
      thead(
        tr(
          th("Rank"),
          th("Player"),
          th(cls := "num-header")("Rating"),
          th(cls := "num-header vol-header")("Vol")
        )
      ),
      tbody(
        players.zipWithIndex.map { case (p, i) => row(i + 1, p) }*
      )
    )
}
