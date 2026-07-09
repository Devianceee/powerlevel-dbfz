package util

import client.model.{LoginRequest, LoginResponse, ReplayRequest, ReplayResponse}
import domain.model.{AuthToken, GameVersion, MatchCharacters, MatchRecord, Player, PlayerId, PlayerName, ReplayId}
import wvlet.airframe.codec.MessageCodec
import scodec.bits.ByteVector
import scodec.codecs.*

import java.nio.ByteBuffer
import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import scala.util.Try

object MessagePackCodec:

  private val genericCodec = MessageCodec.of[Seq[Seq[Any]]]

  private type MetaTuple        = (String, Int, String, String, String, String, String, String)
  private type UserDetailsTuple = (String, String, String, String, Int)
  private type LoginRawTuple    = (MetaTuple, (Int, UserDetailsTuple))

  private val loginResponseCodec = MessageCodec.of[LoginRawTuple]

  private type PlayerTuple = (String, String, String, String, Int)
  private type MatchTuple =
    (Long, Int, String, Seq[Int], Seq[Int], Seq[PlayerTuple], Seq[PlayerTuple], Int, String, Int, Int, Int, String, String, String)
  private type ReplayDataTuple = (Int, Int, Seq[MatchTuple])
  private type ReplayRawTuple  = (Seq[Any], ReplayDataTuple)

  private val replayResponseCodec = MessageCodec.of[ReplayRawTuple]
  private val timestampFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def loginEncoder(loginRequest: LoginRequest): String = {
    val steamIdHex: String = loginRequest.steamId.value.toHexString
    val loginJson          = s"""[["", "", 2,"0.0.3", 103],["${loginRequest.steamId.value}", "$steamIdHex", 256, 0]]"""
    stringToHex(loginJson)
  }

  def replayEncoder(replayRequest: ReplayRequest, token: AuthToken, playerId: PlayerId, gameVersion: GameVersion): String = {
    val replayJson: String = s"""[
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
         |        0,
         |        ${replayRequest.limit},
         |        [
         |            ${gameVersion.value},
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

  private def parseBytesWithCodec[T](bytes: Array[Byte], codec: MessageCodec[T]): Either[String, T] =
    Try(codec.fromMsgPack(bytes)).toEither match {
      case Right(value) => Right(value)
      case Left(err)    => Left(err.getMessage)
    }

  def decodeLoginResponse(bytes: Array[Byte]): Either[String, LoginResponse] =
    parseBytesWithCodec(bytes, loginResponseCodec).map { parsedTuple =>
      LoginResponse(authToken = AuthToken(parsedTuple._1._1), playerId = PlayerId(parsedTuple._2._2._1.toLong))
    }

  def decodeReplayResponse(bytes: Array[Byte]): Either[String, ReplayResponse] =
    parseBytesWithCodec(bytes, replayResponseCodec).map { parsedTuple =>
      val rawMatches: Seq[MatchTuple] = parsedTuple._2._3

      val matchRecordsMap: Map[ReplayId, MatchRecord] =
        rawMatches.flatMap { matchData =>
          val maybeRecord = for {
            winnerTuple <- matchData._6.find(_._1 != "0")
            loserTuple  <- matchData._7.find(_._1 != "0")
          } yield {
            val winner = Player(PlayerId(winnerTuple._1.toLong), PlayerName(winnerTuple._2))
            val loser  = Player(PlayerId(loserTuple._1.toLong), PlayerName(loserTuple._2))

            val record = MatchRecord(
              replayId = ReplayId(matchData._1),
              timestamp = LocalDateTime.parse(matchData._9, timestampFormatter).atOffset(ZoneOffset.UTC),
              winningPlayer = winner,
              winningCharacters = MatchCharacters.fromIds(matchData._4),
              losingPlayer = loser,
              losingCharacters = MatchCharacters.fromIds(matchData._5)
            )
            record.replayId -> record
          }

          maybeRecord
        }.toMap

      ReplayResponse(matchRecordsMap)
    }

  def safeDecodeGenericHex(bytes: Array[Byte]): Either[String, Seq[Seq[Any]]] = parseBytesWithCodec(bytes, genericCodec)
