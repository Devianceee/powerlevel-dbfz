package config

import ciris.*
import ciris.http4s.*
import com.comcast.ip4s.Port

case class ServerConfig(port: Port)

object ServerConfig {
  def read: ConfigValue[Effect, ServerConfig] = {
    val port: ConfigValue[Effect, Port] = env("SERVER_PORT").as[Port].default(Port.fromInt(8080).get)

    port.map(ServerConfig(_))
  }
}
