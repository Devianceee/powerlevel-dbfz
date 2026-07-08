package database

import cats.effect.{IO, Resource}
import com.zaxxer.hikari.HikariConfig
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.log.LogHandler

object Database {
  def resource(dbConfig: DatabaseConfig): Resource[IO, HikariTransactor[IO]] = {
    val hikari = new HikariConfig()

    hikari.setJdbcUrl(s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}")
    hikari.setUsername(dbConfig.user)
    hikari.setPassword(dbConfig.password.value)
    HikariTransactor.fromHikariConfig[IO](hikari)
  }
}
