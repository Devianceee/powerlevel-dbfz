package config

import cats.syntax.all.catsSyntaxTuple2Semigroupal
import ciris.*
import ciris.http4s.*
import com.comcast.ip4s.{Host, Port}

case class ServerConfig(host: Host, port: Port)

object ServerConfig {
  def read: ConfigValue[Effect, ServerConfig] = {
    val host = ciris.env("SERVER_HOST").as[Host]
    val port = ciris.env("SERVER_PORT").as[Port]

    (host, port).mapN(ServerConfig(_, _))
  }
}
