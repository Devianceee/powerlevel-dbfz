package ui.components

import domain.model.database.PlayerTimelineRow
import scalatags.Text.all.*
import ui.model.*
import util.DateFormats

object PlayerPage {

  def view(player: PlayerPageResponse) =
    frag(
      div(cls := "card")(
        div(cls := "player-header")(
          div(
            h2(cls := "player-profile-name")(player.name.value),
            p(
              s"Player ID: ${player.playerId.value}"
            )
          ),

          player.rating.map { rating =>
            div(cls := "player-rating")(
              h1(
                f"$rating%.0f" +
                  player.rd.fold("")(rd => f" ± $rd%.0f")
              ),
              p("Rating")
            )
          }
        ),

        div(cls := "player-summary")(
          stat("Wins", Some(player.wins.toString)),
          stat("Losses", Some(player.losses.toString)),
          stat("Win Rate", Some(f"${player.winRate}%.1f%%")),
          stat("Matches", Some((player.wins + player.losses).toString))
        )
      ),
      div(cls := "card")(
        h2(cls := "leaderboard-title")("Match History"),
        div(cls := "table-container")(
          table(cls := "leaderboard-table")(
            thead(
              tr(
                th("Date"),
                th("Opponent"),
                th(cls := "characters-column")("Your Team"),
                th(cls := "characters-column")("Opponent Team"),
                th("Rating")
              )
            ),
            tbody(player.timeline.map(matchRow)*)
          )
        )
      )
    )

  private def stat(label: String, value: Option[String]) = div(cls := "stat")(span(cls := "stat-label")(label), strong(value))

  private def matchRow(row: PlayerTimelineRow) =
    tr(
      td(
        row.playedAt.format(DateFormats.format)
      ),
      td(
        a(href := s"/player/${row.opponentId.value}")(
          row.opponentName.value
        )
      ),
      td(cls := "characters-column")(
        div(
          cls := s"character-team ${if (row.isWin) "winner-team" else "loser-team"}"
        )(
          CharacterIcons.view(row.playerCharacters)
        )
      ),

      td(cls := "characters-column")(
        div(
          cls := s"character-team ${if (row.isWin) "loser-team" else "winner-team"}"
        )(
          CharacterIcons.view(row.opponentCharacters)
        )
      ),
      td(
        span(
          cls := (if (row.isWin) "rating-up" else "rating-down")
        )(
          f"${row.ratingBefore}%.0f → ${row.ratingAfter}%.0f"
        )
      )
    )
}
