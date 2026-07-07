package ui.components

import domain.model.database.PlayerTimelineRow
import scalatags.Text.all.*
import ui.model.*

object PlayerPage {

  def view(player: PlayerPageResponse) =
    frag(

      div(cls := "card")(
        div(cls := "player-header")(
          div(
            h2(player.name),
            p(s"Player ID: ${player.playerId.value}")
          ),

          div(cls := "player-rating")(
            h1(player.rating.toString),
            p("Rating")
          )
        ),

        div(cls := "player-stats")(
          stat("Wins", player.wins.toString),
          stat("Losses", player.losses.toString),
          stat("Win Rate", f"${player.winRate}%.1f%%"),
          stat("RD", player.rd.map(rd => f"$rd%.1f").getOrElse("-")),
          stat("Volatility", player.volatility.map(v => f"$v%.4f").getOrElse("-"))
        )
      ),

      div(cls := "card")(
        h2("Match History"),

        table(cls := "leaderboard-table")(
          thead(
            tr(
              th("Date"),
              th("Opponent"),
              th("Result"),
              th("Rating")
            )
          ),

          tbody(
            player.timeline.map(matchRow)*
          )
        )
      )
    )

  private def stat(label: String, value: String) =
    div(cls := "stat")(
      span(cls := "stat-label")(label),
      strong(value)
    )

  private def matchRow(row: PlayerTimelineRow) =
    tr(
      td(row.playedAt.toLocalDate.toString),

      td(
        a(
          href := s"/player/${row.opponentId.value}"
        )(
          row.opponentName
        )
      ),

      td(
        if (row.isWin)
          span(cls := "win")("Win")
        else
          span(cls := "loss")("Loss")
      ),

      td(
        f"${row.ratingBefore}%.0f → ${row.ratingAfter}%.0f"
      )
    )
}