package domain.model

import domain.model.Player

import java.time.OffsetDateTime

case class MatchRecord(
    replayId: ReplayId,
    timestamp: OffsetDateTime,
    winningPlayer: Player,
    winningCharacters: MatchCharacters,
    losingPlayer: Player,
    losingCharacters: MatchCharacters
)
