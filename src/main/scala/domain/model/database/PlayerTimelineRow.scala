package domain.model.database

import domain.enums.DbfzCharacter
import domain.model.{MatchCharacters, PlayerId, PlayerName, ReplayId}

import java.time.OffsetDateTime

final case class PlayerTimelineRow(
  replayId: ReplayId,
  playedAt: OffsetDateTime,
  opponentName: PlayerName,
  opponentId: PlayerId,
  isWin: Boolean,

  playerFirst: DbfzCharacter,
  playerSecond: DbfzCharacter,
  playerThird: DbfzCharacter,

  opponentFirst: DbfzCharacter,
  opponentSecond: DbfzCharacter,
  opponentThird: DbfzCharacter,
  
  ratingBefore: Double,
  ratingAfter: Double
) {
  def playerCharacters: MatchCharacters =
    MatchCharacters(
      playerFirst,
      playerSecond,
      playerThird
    )

  def opponentCharacters: MatchCharacters =
    MatchCharacters(
      opponentFirst,
      opponentSecond,
      opponentThird
    )
}
