package ui.views

import scalatags.Text.all.*
import ui.components.*
import ui.model.{LeaderboardRowResponse, PlayerPageResponse}

object Pages {

  def leaderboard(topPlayers: List[LeaderboardRowResponse]) =
    Layout.basePage("Leaderboard")(
      div(cls := "container")(
        PlayerSearch.view,

        div(cls := "card")(
          h2("Top 100 Players"),
          LeaderboardTable.view(topPlayers)
        )
      )
    )

  def player(player: PlayerPageResponse) =
    Layout.basePage(player.name)(
      div(cls := "container")(
        PlayerPage.view(player)
      )
    )

  def about =
    Layout.basePage("About")(
      div(cls := "container")(
        div(cls := "card")(
          h2("About"),

          p(
            """
            PowerLevel is a Dragon Ball FighterZ ranking website built using
            the Glicko2 rating system. It tracks player ratings and provides
            a searchable leaderboard of the strongest players.
            """
          )
        )
      )
    )

  def updates =
    Layout.basePage("Updates")(
      div(cls := "container")(
        div(cls := "card")(
          h2("Updates"),

          ul(
            li("Initial leaderboard implementation"),
            li("Player search using HTMX"),
            li("Top 100 leaderboard"),
            li("ScalaTags server-side rendering"),
            li("Responsive layout")
          )
        )
      )
    )

}
