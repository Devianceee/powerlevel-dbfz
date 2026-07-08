package client

import cats.effect.IO
import client.model.{ReplayRequest, ReplayResponse}
import config.DbfzConfig
import domain.model.{AuthToken, PlayerId}
import org.http4s.{Method, Request, Uri, UrlForm}
import org.http4s.client.Client
import util.MessagePackCodec

trait ReplayClient {
  def getReplays(request: ReplayRequest): IO[ReplayResponse]
}

final case class HttpReplayClient(client: Client[IO], config: DbfzConfig, authToken: AuthToken, playerId: PlayerId) extends ReplayClient {
  override def getReplays(request: ReplayRequest): IO[ReplayResponse] = {
    val body = MessagePackCodec.replayEncoder(request, authToken, playerId, config.gameVersion)
    val req = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"${config.baseUri}/api/catalog/get_replay"))
      .withEntity(UrlForm("data" -> body))

    for {
      rawResp        <- client.expect[Array[Byte]](req)
      replayResponse <- IO.fromEither(MessagePackCodec.decodeReplayResponse(rawResp).left.map(err => new RuntimeException(err)))
    } yield replayResponse
  }
}
