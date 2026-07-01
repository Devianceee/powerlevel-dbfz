package config

import cats.implicits.catsSyntaxTuple4Semigroupal
import ciris._
import ciris.http4s._
import org.http4s.Uri

case class DbfzConfig(baseUri: Uri, gameVersion: Int, steamId: Long, loginId: Long)

object DbfzConfig {
  def read: ConfigValue[Effect, DbfzConfig] = {
    val baseUri: ConfigValue[Effect, Uri] =
      env("DBFZ_BASE_URL").as[Uri].option.map(_.getOrElse(Uri.unsafeFromString("https://dbf.channel.or.jp")))

    val gameVersion: ConfigValue[Effect, Int] =
      env("DBFZ_GAME_VERSION").as[Int]

    val steamId: ConfigValue[Effect, Long] =
      env("DBFZ_STEAM_ID").as[Long]

    val dbfzLoginId: ConfigValue[Effect, Long] =
      env("DBFZ_LOGIN_ID").as[Long]

    (baseUri, gameVersion, steamId, dbfzLoginId).mapN(DbfzConfig(_, _, _, _))
  }
}
