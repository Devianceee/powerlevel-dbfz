package client.model

import domain.model.{MatchRecord, Player, ReplayId}

import java.time.OffsetDateTime

case class ReplayResponse(matches: Map[ReplayId, MatchRecord])
