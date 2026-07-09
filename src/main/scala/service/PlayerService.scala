package service

import cats.effect.IO
import domain.model.database.PlayerTimelineRow
import domain.model.{PlayerId, PlayerName, RatingPoint, ReplayId}
import doobie.Transactor
import doobie.implicits.toConnectionIOOps
import org.typelevel.log4cats.Logger
import query.PlayerQueries
import ui.model.{PlayerPageResponse, PlayerSearchResponse}

trait PlayerService {
  def search(name: PlayerName): IO[List[PlayerSearchResponse]]
  def player(playerId: PlayerId): IO[PlayerPageResponse]
}

final class PlayerServiceImpl(xa: Transactor[IO], playerQueries: PlayerQueries)(using logger: Logger[IO]) extends PlayerService {
  override def search(name: PlayerName): IO[List[PlayerSearchResponse]] =
    (for {
      data <- playerQueries.findPlayers(name = name)
      resp = data.map(p => PlayerSearchResponse(playerId = p.playerId, name = p.name, rating = p.rating))
    } yield resp).transact(xa)

  override def player(playerId: PlayerId): IO[PlayerPageResponse] =
    (for {
      profile     <- playerQueries.playerProfile(playerId)
      timeline    <- playerQueries.playerTimeline(playerId)
      winrate     <- playerQueries.winRate(playerId)
      ratingGraph <- playerQueries.ratingGraph(playerId)
    } yield PlayerPageResponse(
      playerId = profile.playerId,
      name = profile.name,
      rating = profile.rating,
      rd = profile.deviation,
      wins = winrate.wins,
      losses = winrate.losses,
      winRate = winrate.winRate,
      timeline = timeline,
      ratingGraph = ratingGraph
    )).transact(xa)
}
