package domain.model.database

import domain.model.{PlayerId, Rating}

import java.time.Instant

// --- Database Row Models ---
case class DbRatingRow(
    playerId: PlayerId,
    rating: Double,
    deviation: Double,
    volatility: Double
) {
  def toGlicko: Rating = Rating(rating, deviation, volatility)
}
