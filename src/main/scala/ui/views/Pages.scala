package ui.views

import scalatags.Text.all.*
import ui.components.*
import ui.model.{LeaderboardRowResponse, PlayerPageResponse}

object Pages {

  def leaderboard(topPlayers: List[LeaderboardRowResponse]) =
    Layout
      .basePage("Leaderboard")(div(cls := "container")(PlayerSearch.view, div(cls := "card")(h2("Top 100 Players"), LeaderboardTable.view(topPlayers))))

  def player(player: PlayerPageResponse) = Layout.basePage(player.name.value)(div(cls := "container")(PlayerPage.view(player)))

  def about =
    Layout.basePage("About")(
      div(cls := "container")(
        div(cls := "card")(
          h2("About"),
          p("""
            PowerLevel is a Dragon Ball FighterZ ranking website built using
            the Glicko2 rating system. It tracks player ratings and provides
            a searchable leaderboard of the strongest players.
            """),
          h2("What platform does this use?"),
          p("""
            This only counts games on PC (with the rollback setting enabled).
            Games on any other platform DO NOT WORK.
            """),
          h2("I played ranked/ring/tournament mode, why doesn't my games appear?"),
          p("""
            As of right now, this service only counts games from Casual queue as that is what has been told to me that is what mainly people play.
            If this assumption is incorrect, please let me know via Discord or Twitter (X).
            """),
          h2("How do I contact you?"),
          p("""
            Right now, the best way to contact me is either Discord, Twitter or making a GitHub issue.
            Links are in the footer.
            """)
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
            li("Player search using HTMX")
          )
        )
      )
    )

}
