package domain.model

import java.time.{Instant, OffsetDateTime}

case class MatchResult(opponent: Rating, score: Double, timestamp: OffsetDateTime)
