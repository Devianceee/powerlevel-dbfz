package config

import cats.implicits.catsSyntaxTuple4Semigroupal
import ciris._

case class Config(server: ServerConfig, database: DatabaseConfig, pollingConfig: PollingConfig, dbfzConfig: DbfzConfig)

object Config {
  def read: ConfigValue[Effect, Config] =
    (ServerConfig.read, DatabaseConfig.read, PollingConfig.read, DbfzConfig.read).mapN(Config(_, _, _, _))
}
