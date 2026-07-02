package routes

import cats.effect.IO
import org.http4s.HttpRoutes
import service.{LeaderboardService, PlayerService}

final class UiRoutes(leaderboardService: LeaderboardService, playerService: PlayerService) {
  val routes: HttpRoutes[IO] = {
    // Define your API routes here, using the leaderboardService and playerService
    ???
  }
}
