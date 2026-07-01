package client

import cats.effect.IO
import config.DbfzConfig
import org.http4s.client.Client

trait LoginClient {
  def login: IO[AuthToken]
}

final case class HttpLoginClient(client: Client[IO], config: DbfzConfig) extends LoginClient {
  override def login: IO[AuthToken] =
    // Implement the login logic here, e.g., send a request to the login endpoint
    // and return a ReplayClient instance with the obtained auth token.
    ???
}

final case class AuthToken(value: String)
