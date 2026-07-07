package domain.model

import java.time.Instant

// --- Glicko Math Models ---
case class Rating(rating: Double, deviation: Double, volatility: Double) {
  def mu: Double  = (rating - 1500.0) / 173.7178
  def phi: Double = deviation / 173.7178
}

object Rating {
  val default: Rating = Rating(1500.0, 350.0, 0.06)
}