package database

import cats.effect.IO
import config.DatabaseConfig

object FlywayMigration {
  def migrate(dbConfig: DatabaseConfig): IO[Unit] = IO.blocking {
    val flyway = org.flywaydb.core.Flyway
      .configure()
      .dataSource(
        s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}",
        dbConfig.user,
        dbConfig.password.value
      )
      .load()

    flyway.migrate()
  }.void
}
