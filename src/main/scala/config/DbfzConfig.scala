package config

import cats.implicits.catsSyntaxTuple3Semigroupal
import ciris.*
import ciris.http4s.*
import domain.model.Config.given
import domain.model.{GameVersion, SteamId}
import org.http4s.Uri

case class DbfzConfig(baseUri: Uri, gameVersion: GameVersion, steamId: SteamId)

object DbfzConfig {
  def read: ConfigValue[Effect, DbfzConfig] = {
    val baseUri: ConfigValue[Effect, Uri] = env("DBFZ_BASE_URL").as[Uri].default(Uri.unsafeFromString("https://dbf.channel.or.jp"))

    val gameVersion: ConfigValue[Effect, GameVersion] = env("DBFZ_GAME_VERSION").as[GameVersion].default(GameVersion(35))

    val steamId: ConfigValue[Effect, SteamId] = env("DBFZ_STEAM_ID").as[SteamId]

    (baseUri, gameVersion, steamId).mapN(DbfzConfig(_, _, _))
  }
}
