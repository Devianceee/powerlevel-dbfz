package client.model

import domain.model.{PlayerId, ReplayId}
import java.time.OffsetDateTime

case class ReplayResponse(matches: Map[ReplayId, MatchRecord])

case class ReplayPlayer(
    playerId: PlayerId,
    username: String,
    steamId: String
)

case class MatchRecord(
    replayId: ReplayId,
    timestamp: OffsetDateTime,
    winningPlayers: Seq[ReplayPlayer],
    winningCharacters: Seq[Int],
    losingPlayers: Seq[ReplayPlayer],
    losingCharacters: Seq[Int]
)

