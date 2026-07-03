package scheduler

import cats.effect.IO
import client.ReplayClient
import service.ReplayIngestionService

trait ReplayPolling {
  def start: IO[Unit]
}

final class ReplayPollingImpl(ingestionService: ReplayIngestionService) extends ReplayPolling {
  override def start: IO[Unit] =
    // 1) Get the list of replays to process from the replay service
    // 2) Decode the replay data and extract player information
    // 3) Get the player information and ratings from the database
    // 4) Generate new ratings based on the replay data
    // 5) Update all database tables (players, ratings, replays) at once in a single transaction
    ???
}
