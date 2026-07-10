package ui.components

import scalatags.Text.all.*
import ui.model.LeaderboardRowResponse

object LeaderboardTable {

  def row(rank: Int, entry: LeaderboardRowResponse) =
    tr(
      td(cls := "rank")(rank.toString),
      td(
        a(href := s"/player/${entry.playerId}")(
          entry.name.value
        )
      ),
      td(cls := "rating-cell")(
        span(cls := "rating-main")(
          f"${entry.rating}%.0f"
        ),
        span(
          cls   := "rating-deviation",
          title := "Rating deviation. Lower means the rating is more reliable."
        )(
          f" ± ${entry.rd}%.0f"
        )
      )
    )

  def view(players: List[LeaderboardRowResponse]) =
    div(
      div(cls := "leaderboard-info")(
        "Only players with a rating deviation < 75 will appear on the leaderboard"
      ),
      div(cls := "table-container")(
        table(cls := "leaderboard-table")(
          thead(
            tr(
              th("Rank"),
              th("Player"),
              th(cls := "num-header")("Rating")
            )
          ),
          tbody(
            players.zipWithIndex.map { case (p, i) =>
              row(i + 1, p)
            }*
          )
        )
      )
    )
}
