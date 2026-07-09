package ui.views

import scalatags.Text.all.*
import ui.components.*
import ui.model.{LatestReplayRowResponse, LeaderboardRowResponse, PlayerPageResponse, SiteUpdate}

object Pages {

  def leaderboard(topPlayers: List[LeaderboardRowResponse]) =
    Layout
      .basePage("Leaderboard")(div(cls := "container")(PlayerSearch.view, div(cls := "card")(h2("Top 100 Players"), LeaderboardTable.view(topPlayers))))

  def player(player: PlayerPageResponse) = Layout.basePage(player.name.value)(div(cls := "container")(PlayerPage.view(player)))

  def latestReplays(replays: List[LatestReplayRowResponse]) = Layout.basePage("Latest matches")(div(cls := "container")(LatestReplays.view(replays)))

  def about =
    Layout.basePage("About")(
      div(cls := "container")(
        div(cls := "card")(
          h2("About"),
          p("""
            PowerLevel is a DRAGON BALL FighterZ ranking website built using
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
          h2("I've played some matches in Casual queue, why aren't I appearing on the leaderboard?"),
          p("""
            The leaderboard requires you to have a deviation of 75 or less. This is to make sure that your rating is not inflated (Lichess applies the same principle).
            To verify you're in the database, you can search your name and you should appear.
            """),
          h2("What does \'Established\' and \'Provisional\' mean?"),
          p("""
            If your deviation is 75 or less, you're 'Established' to show your rating is stable. Otherwise you will be labeled as 'Provisional'.
            """),
          h2("How long does it take for my results to appear?"),
          p("""
            The server polls every 60 seconds the last 999 replays and then stores the ones we haven't seen.
            """),
          h2("How do I contact you?"),
          p("""
            Right now, the best way to contact me is either Discord, Twitter or making a GitHub issue.
            Links are in the footer.
            """)
        )
      )
    )

  def updates(updates: List[SiteUpdate]) =
    Layout.basePage("Updates")(
      div(cls := "container")(
        h1("Updates"),
        UpdatesList.view(updates)
      )
    )
}
