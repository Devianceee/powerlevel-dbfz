package routes

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io.*
import domain.model.Circe.given
import domain.model.PlayerName
import service.{LeaderboardService, PlayerService, ReplayService}

final class ApiRoutes(leaderboardService: LeaderboardService, playerService: PlayerService, replayService: ReplayService) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "api" =>
      Ok("To be completed later")

    case GET -> Root / "api" / "v1" / "leaderboard" => {
      for {
        players  <- leaderboardService.getLeaderboard
        response <- Ok(players)
      } yield response
    }

    case GET -> Root / "api" / "v1" / "players" / "search" :? NameQueryParam(name) => {
      for {
        players  <- playerService.search(PlayerName(name))
        response <- Ok(players)
      } yield response
    }

    case GET -> Root / "api" / "v1" / "latest" =>
      for {
        replays  <- replayService.getLatest
        response <- Ok(replays)
      } yield response
  }
}

object NameQueryParam extends QueryParamDecoderMatcher[String]("name")
