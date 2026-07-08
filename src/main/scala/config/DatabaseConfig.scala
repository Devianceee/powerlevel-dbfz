package config

import cats.implicits.catsSyntaxTuple5Semigroupal
import ciris._

case class DatabaseConfig(host: String, port: Int, user: String, password: Secret[String], database: String)

object DatabaseConfig {
  def read: ConfigValue[Effect, DatabaseConfig] = {
    val host     = env("DB_HOST").default("localhost")
    val port     = env("DB_PORT").as[Int].default(5432)
    val user     = env("DB_USER").default("powerlevel")
    val password = env("DB_PASSWORD").default("password").secret
    val database = env("DB_NAME").default("powerlevel")

    (host, port, user, password, database).mapN(DatabaseConfig.apply)
  }
}
