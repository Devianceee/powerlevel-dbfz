package config

import cats.implicits.catsSyntaxTuple3Semigroupal
import ciris.*
import ciris.http4s.*
import domain.model.SteamId
import org.http4s.Uri

case class DbfzConfig(baseUri: Uri, gameVersion: Int, steamId: SteamId)

object DbfzConfig {
  def read: ConfigValue[Effect, DbfzConfig] = {
    val baseUri: ConfigValue[Effect, Uri] =
      env("DBFZ_BASE_URL").as[Uri].default(Uri.unsafeFromString("https://dbf.channel.or.jp"))

    val gameVersion: ConfigValue[Effect, Int] =
      env("DBFZ_GAME_VERSION").as[Int].default(35)

    val steamId: ConfigValue[Effect, SteamId] =
      env("DBFZ_STEAM_ID").as[SteamId].default(SteamId(1111))

    (baseUri, gameVersion, steamId).mapN(DbfzConfig(_, _, _))
  }
}
