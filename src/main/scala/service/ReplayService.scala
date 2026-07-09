package service

import cats.effect.IO
import doobie.implicits.toConnectionIOOps
import doobie.util.transactor.Transactor
import query.ReplaysQueries
import ui.model.LatestReplayRowResponse

import java.time.format.DateTimeFormatter

trait ReplayService {
  def getLatest: IO[List[LatestReplayRowResponse]]
}

final class ReplayServiceImpl(xa: Transactor[IO], replaysQueries: ReplaysQueries) extends ReplayService {
  private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  override def getLatest: IO[List[LatestReplayRowResponse]] =
    (for {
      data <- replaysQueries.latest()
      resp = data.map(r =>
        LatestReplayRowResponse(
          replayId = r.replayId,
          timestamp = r.timestamp,
          winnerId = r.winnerId,
          winner = r.winner,
          loserId = r.loserId,
          loser = r.loser,
          winnerCharacters = r.winnerCharacters,
          loserCharacters = r.loserCharacters
        )
      )
    } yield resp).transact(xa)
}
