package domain

import ciris.ConfigDecoder
import doobie.Meta
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}
import ui.model.{LeaderboardRowResponse, PlayerSearchResponse}

package object model {
  object Metas {
    given replayIdMeta: Meta[ReplayId] = Meta[Long].imap[ReplayId](ReplayId(_))(_.value)
    given playerIdMeta: Meta[PlayerId] = Meta[Long].imap[PlayerId](PlayerId(_))(_.value)
  }

  object Circe {
    given playerIdEncoder: Encoder[PlayerId]                             = Encoder.encodeLong.contramap(_.value)
    given playerIdDecoder: Decoder[PlayerId]                             = Decoder.decodeLong.map(PlayerId.apply)
    given leaderboardRowResponseEncoder: Encoder[LeaderboardRowResponse] = deriveEncoder[LeaderboardRowResponse]
    given playerSearchResponseEncoder: Encoder[PlayerSearchResponse]     = deriveEncoder[PlayerSearchResponse]

  }

  object Config {
    given gameVersionDecoder: ConfigDecoder[String, GameVersion] = ConfigDecoder[String, Int].map(GameVersion.apply)
    given steamIdDecoder: ConfigDecoder[String, SteamId] = ConfigDecoder[String, String].mapOption("SteamId")(_.toLongOption.map(SteamId.apply))
  }
}
