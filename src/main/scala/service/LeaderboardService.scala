package service

import cats.effect.IO
import domain.model.{Player, PlayerId, Rating}
import query.LeaderboardQueries
import repository.PlayerRepository
import ui.model.LeaderboardRowResponse

trait LeaderboardService {
  def getTop100Players: IO[List[LeaderboardRowResponse]]
}

final class LeaderboardServiceImpl(leaderboardQueries: LeaderboardQueries) extends LeaderboardService {
  override def getTop100Players: IO[List[LeaderboardRowResponse]] =
    for {
      data <- leaderboardQueries.globalLeaderboard()
      resp = data.map(p => LeaderboardRowResponse(playerId = p.playerId, name = p.name, rating = p.rating, rd = p.rd, volatility = p.volatility))
    } yield resp
}
