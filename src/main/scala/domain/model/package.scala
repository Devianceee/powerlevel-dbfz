package domain

import ciris.ConfigDecoder
import domain.enums.DbfzCharacter
import doobie.Meta
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import ui.model.{LeaderboardRowResponse, PlayerSearchResponse}

package object model {
  object Metas {
    given replayIdMeta: Meta[ReplayId] = Meta[Long].imap[ReplayId](ReplayId(_))(_.value)

    given playerIdMeta: Meta[PlayerId] = Meta[Long].imap[PlayerId](PlayerId(_))(_.value)

    given playerNameMeta: Meta[PlayerName] = Meta[String].imap[PlayerName](PlayerName(_))(_.value)

    given dbfzCharacterMeta: Meta[DbfzCharacter] =
      Meta[Short].timap { id =>
        DbfzCharacter.fromId(id.toInt).getOrElse(
          throw new IllegalArgumentException(
            s"Unknown DBFZ character id: $id"
          )
        )
      }(_.id.toShort)
  }

  object Circe {
    given playerIdEncoder: Encoder[PlayerId]                             = Encoder.encodeLong.contramap(_.value)
    given playerIdDecoder: Decoder[PlayerId]                             = Decoder.decodeLong.map(PlayerId.apply)
    given replayIdEncoder: Encoder[ReplayId]                             = Encoder.encodeLong.contramap(_.value)
    given replayIdDecoder: Decoder[ReplayId]                             = Decoder.decodeLong.map(ReplayId.apply)
    given playerNameEncoder: Encoder[PlayerName]                         = Encoder.encodeString.contramap(_.value)
    given playerNameDecoder: Decoder[PlayerName]                         = Decoder.decodeString.map(PlayerName.apply)
    given leaderboardRowResponseEncoder: Encoder[LeaderboardRowResponse] = deriveEncoder[LeaderboardRowResponse]
    given playerSearchResponseEncoder: Encoder[PlayerSearchResponse]     = deriveEncoder[PlayerSearchResponse]

    given dbfzCharacterEncoder: Encoder[DbfzCharacter] =
      Encoder.instance { character =>
        Json.obj(
          "key"   -> Json.fromString(character.entryName),
          "id"    -> Json.fromInt(character.id),
          "name"  -> Json.fromString(character.displayName),
          "image" -> Json.fromString(character.imagePath)
        )
      }

    given dbfzCharacterDecoder: Decoder[DbfzCharacter] =
      Decoder.instance { cursor =>
        cursor
          .get[String]("key")
          .flatMap { key =>
            DbfzCharacter.values
              .find(_.entryName == key)
              .toRight(
                DecodingFailure(
                  s"Unknown DBFZ character: $key",
                  cursor.history
                )
              )
          }
      }
  }

  object Config {
    given gameVersionDecoder: ConfigDecoder[String, GameVersion] = ConfigDecoder[String, Int].map(GameVersion.apply)
    given steamIdDecoder: ConfigDecoder[String, SteamId]         = ConfigDecoder[String, String].mapOption("SteamId")(_.toLongOption.map(SteamId.apply))
  }
}
