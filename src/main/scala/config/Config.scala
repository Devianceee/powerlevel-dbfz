package config

import cats.effect.IO
import cats.implicits.catsSyntaxTuple4Semigroupal
import ciris.*

case class Config(server: ServerConfig, database: DatabaseConfig, pollingConfig: PollingConfig, dbfzConfig: DbfzConfig)

object Config {
  def read: IO[Config] =
    (ServerConfig.read, DatabaseConfig.read, PollingConfig.read, DbfzConfig.read).mapN(Config(_, _, _, _)).load[IO]
}
