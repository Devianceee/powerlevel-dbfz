package routes

import cats.effect.IO
import domain.model.PlayerId
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import service.{LeaderboardService, PlayerService}
import ui.components.PlayerSearch
import ui.views.Pages.*

import scala.annotation.unused

final class UiRoutes(leaderboardService: LeaderboardService, playerService: PlayerService) {
  implicit val htmlEncoder: EntityEncoder[IO, String] =
    EntityEncoder.stringEncoder[IO].withContentType(`Content-Type`(MediaType.text.html))

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root => // Homepage
      leaderboardService.getTop100Players.flatMap { players =>
        Ok(
          leaderboard(players).render
        )
      }

    case req @ GET -> Root / "player" / "search" =>
      val q = req.params.getOrElse("q", "")
      playerService.search(q).flatMap { players =>
        Ok(
          PlayerSearch.results(players).render
        )
      }

    case req @ GET -> Root / "player" / LongVar(playerId) =>
      playerService.player(PlayerId(playerId)).flatMap { players =>
        Ok(
          player(players).render
        )
      }

    case GET -> Root / "about" =>
      Ok(about.render)

    case GET -> Root / "updates" =>
      Ok(updates.render)
    }
}
