package ui.components

import scalatags.Text.all.*
import ui.model.LatestReplayRowResponse
import util.DateFormats

object LatestReplays {

  def view(replays: List[LatestReplayRowResponse]) =
    div(cls := "latest-replays")(
      h2("Latest Replays"),
      div(cls := "replay-list")(
        replays.map(replay)
      )
    )

  private def replay(row: LatestReplayRowResponse) =
    div(cls := "replay-card")(
      div(cls := "replay-time")(
        row.timestamp.format(DateFormats.format)
      ),
      div(cls := "replay-players")(
        div(cls := "winner")(
          span(cls := "win")("Winner"),
          span(cls := "replay-player-name")(
            a(href := s"/player/${row.winnerId.value}")(
              row.winner.value
            )
          ),
          CharacterIcons.view(row.winnerCharacters)
        ),
        div(cls := "vs")(
          "VS"
        ),
        div(cls := "loser")(
          span(cls := "loss")("Loser"),
          span(cls := "replay-player-name")(
            a(href := s"/player/${row.loserId.value}")(
              row.loser.value
            )
          ),
          CharacterIcons.view(row.loserCharacters)
        )
      )
    )
}
