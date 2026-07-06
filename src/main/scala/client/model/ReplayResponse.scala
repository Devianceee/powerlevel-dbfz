package client.model

import domain.model.{Player, ReplayId}

import java.time.OffsetDateTime

case class ReplayResponse(matches: Map[ReplayId, MatchRecord])

case class MatchRecord(
    replayId: ReplayId,
    timestamp: OffsetDateTime,
    winningPlayers: Seq[Player],
    winningCharacters: Seq[Int],
    losingPlayers: Seq[Player],
    losingCharacters: Seq[Int]
)
