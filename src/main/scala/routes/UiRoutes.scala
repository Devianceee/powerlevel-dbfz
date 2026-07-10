package routes

import cats.effect.IO
import domain.model.{PlayerId, PlayerName}
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import service.{LeaderboardService, PlayerService, ReplayService}
import ui.components.PlayerSearch
import ui.data.Updates
import ui.views.Pages

final class UiRoutes(leaderboardService: LeaderboardService, playerService: PlayerService, replayService: ReplayService) {
  implicit val htmlEncoder: EntityEncoder[IO, String] = EntityEncoder.stringEncoder[IO].withContentType(`Content-Type`(MediaType.text.html))

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root => // Homepage
      leaderboardService.getLeaderboard.flatMap { players => Ok(Pages.homepage(players).render) }

    case req @ GET -> Root / "player" / "search" =>
      val q = req.params.getOrElse("q", "")
      playerService.search(PlayerName(q)).flatMap { players => Ok(PlayerSearch.results(players).render) }

    case req @ GET -> Root / "player" / LongVar(playerId) =>
      playerService.player(PlayerId(playerId)).flatMap { players =>
        Ok(Pages.player(players).render)
      }

    case GET -> Root / "latest" =>
      replayService.getLatest.flatMap(r => Ok(Pages.latestReplays(r).render))

    case GET -> Root / "about" => Ok(Pages.about.render)
    case GET -> Root / "updates" =>
      Updates.load.flatMap { updates =>
        Ok(Pages.updates(updates).render)
      }
  }
}
