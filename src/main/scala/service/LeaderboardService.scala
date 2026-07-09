package service

import cats.effect.IO
import doobie.implicits.toConnectionIOOps
import doobie.util.transactor.Transactor
import query.LeaderboardQueries
import ui.model.LeaderboardRowResponse

trait LeaderboardService {
  def getLeaderboard: IO[List[LeaderboardRowResponse]]
}

final class LeaderboardServiceImpl(xa: Transactor[IO], leaderboardQueries: LeaderboardQueries) extends LeaderboardService {
  override def getLeaderboard: IO[List[LeaderboardRowResponse]] =
    (for {
      data <- leaderboardQueries.globalLeaderboard()
      resp = data.map(p => LeaderboardRowResponse(playerId = p.playerId, name = p.name, rating = p.rating, rd = p.rd))
    } yield resp).transact(xa)
}
