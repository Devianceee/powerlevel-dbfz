package client

import cats.effect.IO
import org.http4s.client.Client

trait ReplayClient {
  def getReplayData(replayId: String): IO[Unit]
}

final case class HttpReplayClient(client: Client[IO], authToken: AuthToken) extends ReplayClient {
  override def getReplayData(replayId: String): IO[Unit] = {
    // Implement the logic to fetch replay data using the auth token and replay ID
    // For example, send a request to the replay endpoint with the auth token
    ???
  }
}
