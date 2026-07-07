package domain.model.database

import domain.model.{MatchCharacters, PlayerId, ReplayId}

import java.time.OffsetDateTime

case class DbReplayRow(
    id: ReplayId,
    winnerId: PlayerId,
    loserId: PlayerId,
    winnerCharacters: MatchCharacters,
    loserCharacters: MatchCharacters,
    playedAt: OffsetDateTime
)
