package routes

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io.*
import domain.model.Circe.playerIdEncoder
import service.{LeaderboardService, PlayerService}

final class ApiRoutes(leaderboardService: LeaderboardService, playerService: PlayerService) {
  val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {

      case GET -> Root / "api" / "v1" / "leaderboard" =>
        for {
          players  <- leaderboardService.getTop100Players
          response <- Ok(players)
        } yield response

      case GET -> Root / "api" / "v1" / "players" / "search" :? NameQueryParam(name) =>
        for {
          players  <- playerService.search(name)
          response <- Ok(players)
        } yield response
    }
}

object NameQueryParam extends QueryParamDecoderMatcher[String]("name")
