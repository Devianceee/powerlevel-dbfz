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
            h2(
              cls := "player-profile-name"
            )(player.name.value),
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
        )
      ),
      div(cls := "card")(
        h2("Match History"),
        table(cls := "leaderboard-table")(
          thead(
            tr(
              th("Date"),
              th("Opponent"),
              th(cls := "characters-column")("Characters"),
              th("Result"),
              th("Rating")
            )
          ),
          tbody(player.timeline.map(matchRow)*)
        )
      )
    )

  private def stat(label: String, value: Option[String]) = div(cls := "stat")(span(cls := "stat-label")(label), strong(value))

  private def matchRow(row: PlayerTimelineRow) =
    tr(
      td(
        row.playedAt.toLocalDate.toString
      ),
      td(
        a(href := s"/player/${row.opponentId.value}")(
          row.opponentName.value
        )
      ),
      td(cls := "characters-column")(
        div(cls := "matchup-characters")(
          div(
            cls := s"character-team ${if (row.isWin) "winner-team" else "loser-team"}"
          )(
            CharacterIcons.view(row.playerCharacters)
          ),
          div(
            cls := s"character-team ${if (row.isWin) "loser-team" else "winner-team"}"
          )(
            CharacterIcons.view(row.opponentCharacters)
          )
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
