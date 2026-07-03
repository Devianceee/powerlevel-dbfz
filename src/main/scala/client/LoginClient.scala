package client

import cats.effect.IO
import client.model.{LoginRequest, LoginResponse}
import config.DbfzConfig
import org.http4s.{Method, Request, Uri}
import org.http4s.client.Client
import util.MessagePackCodec

trait LoginClient {
  def login: IO[LoginResponse]
}

final case class HttpLoginClient(client: Client[IO], config: DbfzConfig) extends LoginClient {
  override def login: IO[LoginResponse] = {
    val body = MessagePackCodec.loginEncoder(LoginRequest(config.steamId))
    val req  = Request[IO](
      method = Method.POST,
      uri = Uri.unsafeFromString(s"${config.baseUri}/api/user/login")
    ).withEntity(body)

    for {
      rawHexResponse <- client.expect[String](req)
      loginResponse  <- IO.fromEither(
        MessagePackCodec.decodeLoginResponse(rawHexResponse).left.map(err => new RuntimeException(err))
      )
    } yield loginResponse
  }
}
