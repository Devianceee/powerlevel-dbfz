package config

import cats.implicits.catsSyntaxTuple5Semigroupal
import ciris._
import ciris.http4s._
import com.comcast.ip4s.{Host, Port}

case class DatabaseConfig(host: Host, port: Port, user: String, password: Secret[String], database: String)

object DatabaseConfig {
  def read: ConfigValue[Effect, DatabaseConfig] = {
    val host     = env("DB_HOST").as[Host]
    val port     = env("DB_PORT").as[Port]
    val user     = env("DB_USER")
    val password = env("DB_PASSWORD").secret
    val database = env("DB_NAME")

    (host, port, user, password, database).mapN(DatabaseConfig(_, _, _, _, _))
  }
}
