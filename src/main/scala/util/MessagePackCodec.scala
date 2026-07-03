package util

import client.model.{LoginRequest, ReplayRequest, LoginResponse, ReplayResponse, MatchRecord, ReplayPlayer}
import domain.model.{AuthToken, PlayerId, ReplayId}
import wvlet.airframe.codec.MessageCodec
import scodec.bits.ByteVector
import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object MessagePackCodec:

  private val genericCodec = MessageCodec.of[Seq[Seq[Any]]]

  private type MetaTuple        = (String, Int, String, String, String, String, String, String)
  private type UserDetailsTuple = (String, String, String, String, Int)
  private type LoginRawTuple    = (MetaTuple, (Int, UserDetailsTuple))

  private val loginResponseCodec = MessageCodec.of[LoginRawTuple]

  private type PlayerTuple = (String, String, String, String, Int)
  private type MatchTuple  = (
      Long,
      Int,
      String,
      Seq[Int],
      Seq[Int],
      Seq[PlayerTuple],
      Seq[PlayerTuple],
      Int,
      String,
      Int,
      Int,
      Int,
      String,
      String,
      String
  )
  private type ReplayDataTuple = (Int, Int, Seq[MatchTuple])
  private type ReplayRawTuple  = (Seq[Any], ReplayDataTuple)

  private val replayResponseCodec = MessageCodec.of[ReplayRawTuple]
  private val timestampFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def loginEncoder(loginRequest: LoginRequest): String = {
    val steamIdHex = ByteVector.fromLong(loginRequest.steamId.value).toHex
    val loginJson  = s"""[["", "", 2,"0.0.3", 103],["${loginRequest.steamId.value}", "$steamIdHex", 256, 0]]"""
    stringToHex(loginJson)
  }

  def replayEncoder(replayRequest: ReplayRequest, token: AuthToken, playerId: PlayerId): String = {
    val replayJson: String =
      s"""[
         |    [
         |        "${playerId.value}",
         |        "${token.value}",
         |        2,
         |        "0.0.3",
         |        103
         |    ],
         |    [
         |        7,
         |        1,
         |        1,
         |        ${replayRequest.limit},
         |        [
         |            28,
         |            ${replayRequest.character},
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
    val parsedData = genericCodec.fromJson(jsonString)
    val bytes      = genericCodec.toMsgPack(parsedData)
    ByteVector.view(bytes).toHex
  }

  private def parseHexWithCodec[T](hexString: String, codec: MessageCodec[T]): Either[String, T] =
    ByteVector.fromHex(hexString) match
      case Some(byteVector) => Right(codec.fromMsgPack(byteVector.toArray))
      case None             => Left(s"Server returned invalid hex: $hexString")

  def decodeLoginResponse(hexString: String): Either[String, LoginResponse] =
    parseHexWithCodec(hexString, loginResponseCodec).map { parsedTuple =>
      LoginResponse(
        authToken = AuthToken(parsedTuple._1._1),
        playerId = PlayerId(parsedTuple._2._2._1.toLong)
      )
    }

  def decodeReplayResponse(hexString: String): Either[String, ReplayResponse] =
    parseHexWithCodec(hexString, replayResponseCodec).map { parsedTuple =>
      val rawMatches = parsedTuple._2._3

      val matchRecordsMap = rawMatches.map { matchData =>
        val winners = matchData._6
          .filter(_._1 != "0")
          .map(p => ReplayPlayer(PlayerId(p._1.toLong), p._2, p._3))

        val losers = matchData._7
          .filter(_._1 != "0")
          .map(p => ReplayPlayer(PlayerId(p._1.toLong), p._2, p._3))

        val record = MatchRecord(
          replayId = ReplayId(matchData._1),
          timestamp = LocalDateTime.parse(matchData._9, timestampFormatter).atOffset(ZoneOffset.UTC),
          winningPlayers = winners,
          winningCharacters = matchData._4,
          losingPlayers = losers,
          losingCharacters = matchData._5
        )

        record.replayId -> record
      }.toMap

      ReplayResponse(matchRecordsMap)
    }

  def safeDecodeGenericHex(hexString: String): Either[String, Seq[Seq[Any]]] =
    parseHexWithCodec(hexString, genericCodec)
