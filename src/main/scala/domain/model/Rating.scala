package domain.model

import java.time.Instant

// --- Glicko Math Models ---
case class Rating(rating: Double, rd: Double, vol: Double) {
  def mu: Double  = (rating - 1500.0) / 173.7178
  def phi: Double = rd / 173.7178
}

object Rating {
  val default: Rating = Rating(1500.0, 350.0, 0.06)
}

case class MatchResult(opponent: Rating, score: Double, timestamp: Instant)

// --- Database Row Models ---
case class DbRatingRow(
    playerId: Long,
    rating: Double,
    deviation: Double,
    volatility: Double,
    updatedAt: Instant
) {
  def toGlicko: Rating = Rating(rating, deviation, volatility)
}

case class ReplayRow(
    id: Long,
    winnerId: Long,
    loserId: Long,
    playedAt: Instant
)

case class RatingHistoryInsert(
    replayId: Long,
    playerId: Long,
    ratingBefore: Double,
    ratingAfter: Double,
    deviationBefore: Double,
    deviationAfter: Double,
    volatilityBefore: Double,
    volatilityAfter: Double
)
