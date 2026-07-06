ThisBuild / scalaVersion := "3.3.8"

val CatsEffectVersion = "3.7.0"
val Http4sVersion     = "0.23.32"
val CirceVersion      = "0.14.14"
val DoobieVersion     = "1.0.0-RC10"
val TapirVersion      = "1.11.49"
val ScalatagsVersion  = "0.13.1"
val CirisConfig       = "2.4.0"
val FlywayVersion     = "11.12.0"

lazy val root = (project in file("."))
  .settings(
    name := "powerlevel-dbfz",
    libraryDependencies ++= Seq(
      // Cats Effect
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,

      // HTTP Server / Client
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-dsl"          % Http4sVersion,
      "org.http4s" %% "http4s-circe"        % Http4sVersion,

      // JSON
      "io.circe" %% "circe-core"    % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser"  % CirceVersion,

      // Tapir
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"      % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion,

      // Database
      "org.tpolecat"  %% "doobie-core"     % DoobieVersion,
      "org.tpolecat"  %% "doobie-hikari"   % DoobieVersion,
      "org.tpolecat"  %% "doobie-postgres" % DoobieVersion,
      "org.postgresql" % "postgresql"      % "42.7.8",

      // Migrations
      "org.flywaydb" % "flyway-core"                % FlywayVersion,
      "org.flywaydb" % "flyway-database-postgresql" % FlywayVersion,

      // Configuration
      "is.cir" %% "ciris"        % CirisConfig,
      "is.cir" %% "ciris-circe"  % CirisConfig,
      "is.cir" %% "ciris-http4s" % CirisConfig,

      // HTML
      "com.lihaoyi" %% "scalatags" % ScalatagsVersion,

      // Logging
      "org.typelevel" %% "log4cats-slf4j"  % "2.7.1",
      "ch.qos.logback" % "logback-classic" % "1.5.18",

      "org.wvlet.airframe" %% "airframe-msgpack" % "2026.2.2",
      "org.wvlet.airframe" %% "airframe-codec"   % "2026.2.2",

      // Tests
      "org.scalameta" %% "munit"             % "1.2.1" % Test,
      "org.typelevel" %% "munit-cats-effect" % "2.1.0" % Test
    ),

    assembly / mainClass       := Some("Main"),
    assembly / assemblyJarName := "app.jar",

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
//      "-Wunused:all",
//      "-Xfatal-warnings",
      "-Wnonunit-statement"
    )
  )

ThisBuild / assemblyMergeStrategy := {
  case x if x.contains("module-info.class") => MergeStrategy.discard
  case x                                    => MergeStrategy.defaultMergeStrategy(x)
}
