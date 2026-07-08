package ui.components

import scalatags.Text.all.*
import ui.model.LeaderboardRowResponse

object LeaderboardTable {

  def row(rank: Int, entry: LeaderboardRowResponse) =
    tr(td(cls := "rank")(rank.toString), td(a(href := s"/player/${entry.playerId}")(entry.name.value)), td(cls := "rating")(f"${entry.rating}"))

  def view(players: List[LeaderboardRowResponse]) =
    table(cls := "leaderboard")(thead(tr(th("Rank"), th("Player"), th("Rating"))), tbody(players.zipWithIndex.map { case (p, i) => row(i + 1, p) }*))
}
