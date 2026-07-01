package config

import cats.implicits.catsSyntaxTuple5Semigroupal
import ciris._
import ciris.http4s._

case class DatabaseConfig(host: String, port: Int, user: String, password: Secret[String], database: String)

object DatabaseConfig {
  def read: ConfigValue[Effect, DatabaseConfig] = {
    val host     = env("DB_HOST").option.map(_.getOrElse("localhost"))
    val port     = env("DB_PORT").as[Int].option.map(_.getOrElse(5432))
    val user     = env("DB_USER").option.map(_.getOrElse("powerlevel"))
    val password = env("DB_PASSWORD").option.map(_.getOrElse("password")).secret
    val database = env("DB_NAME").option.map(_.getOrElse("powerlevel"))

    (host, port, user, password, database)
      .mapN(DatabaseConfig.apply)
  }
}
