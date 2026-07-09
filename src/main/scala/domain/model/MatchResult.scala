package domain.model

import java.time.OffsetDateTime

case class MatchResult(opponent: Rating, score: Double, timestamp: OffsetDateTime)
