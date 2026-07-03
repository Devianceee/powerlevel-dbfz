package client

import cats.effect.IO
import client.model.{ReplayRequest, ReplayResponse}
import domain.model.{AuthToken, PlayerId}
import org.http4s.client.Client
import util.MessagePackCodec

trait ReplayClient {
  def getReplays(request: ReplayRequest): IO[ReplayResponse]
}

final case class HttpReplayClient(client: Client[IO], authToken: AuthToken, playerId: PlayerId) extends ReplayClient {
  override def getReplays(request: ReplayRequest): IO[ReplayResponse] = {
    val body = MessagePackCodec.replayEncoder(request, authToken, playerId)
    val req  = Request[IO](
      method = Method.POST,
      uri = Uri.unsafeFromString(s"${config.baseUri}/api/catalog/get_replay")
    ).withEntity(body)

    for {
      rawHexResponse <- client.expect[String](req)
      replayResponse <- IO.fromEither(
        MessagePackCodec.decodeReplayResponse(rawHexResponse).left.map(err => new RuntimeException(err))
      )
    } yield replayResponse
  }
}
