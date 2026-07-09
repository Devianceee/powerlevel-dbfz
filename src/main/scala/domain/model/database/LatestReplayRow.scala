package domain.model.database

import domain.model.{MatchCharacters, PlayerId, PlayerName, ReplayId}

import java.time.OffsetDateTime

case class LatestReplayRow(
  replayId: ReplayId,
  timestamp: OffsetDateTime,
  winnerId: PlayerId,
  winner: PlayerName,
  loserId: PlayerId,
  loser: PlayerName,
  winnerCharacters: MatchCharacters,
  loserCharacters: MatchCharacters
)
