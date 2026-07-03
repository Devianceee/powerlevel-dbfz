package util

import client.AuthToken
import client.model.{Login, Replay}
import wvlet.airframe.codec.MessageCodec
import scodec.bits.ByteVector

object MessagePackCodec:
  // Tell Airframe to expect a generic Array of Arrays containing mixed types
  private val codec = MessageCodec.of[Seq[Seq[Any]]]

  def loginEncoder(login: Login): String = {
    val steamIdHex = ByteVector.fromLong(login.steamId.value).toHex
    val loginJson  = s"""[["", "", 2,"0.0.3", 3],["${login.steamId.value}", "$steamIdHex", 256, 0]]"""
    stringToHex(loginJson)
  }

  def replayEncoder(replay: Replay, token: AuthToken): String = {
    val replayJson: String =
      s"""[
         |    [
         |        "${replay.playerId.value}",
         |        "${token.value}",
         |        2,
         |        "0.0.3",
         |        103
         |    ],
         |    [
         |        7,
         |        1,
         |        1,
         |        ${replay.limit},
         |        [
         |            28,
         |            ${replay.character},
         |            104,
         |            1,
         |            -1,
         |            [
         |            ]
         |        ]
         |    ]
         |]""".stripMargin

    stringToHex(replayJson)
  }

  private def stringToHex(jsonString: String): String = {
    val parsedData = codec.fromJson(jsonString)
    val bytes      = codec.toMsgPack(parsedData)
    ByteVector.view(bytes).toHex
  }
