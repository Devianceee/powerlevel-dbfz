ThisBuild / scalaVersion := "3.3.8"

val CatsEffectVersion = "3.7.0"
val Http4sVersion = "0.23.32"
val CirceVersion = "0.14.14"
val DoobieVersion = "1.0.0-RC10"
val TapirVersion = "1.11.49"
val ScalatagsVersion = "0.13.1"
val PureConfigVersion = "0.17.9"
val FlywayVersion = "11.12.0"

lazy val root = (project in file("."))
  .settings(
    name := "powerlevel-dbfz",
    libraryDependencies ++= Seq(

      // Cats Effect
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,

      // HTTP Server / Client
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,

      // JSON
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,

      // Tapir
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion,

      // Database
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.postgresql" % "postgresql" % "42.7.8",

      // Migrations
      "org.flywaydb" % "flyway-core" % FlywayVersion,

      // Configuration
      "com.github.pureconfig" %% "pureconfig-core" % PureConfigVersion,

      // HTML
      "com.lihaoyi" %% "scalatags" % ScalatagsVersion,

      // Logging
      "org.typelevel" %% "log4cats-slf4j" % "2.7.1",
      "ch.qos.logback" % "logback-classic" % "1.5.18",

      // Tests
      "org.scalameta" %% "munit" % "1.2.1" % Test,
      "org.typelevel" %% "munit-cats-effect" % "2.1.0" % Test
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Wunused:all",
      "-Wnonunit-statement"
    )
  )