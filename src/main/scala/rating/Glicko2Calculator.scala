package rating

import domain.model.{MatchResult, Rating, RatingUpdate}

import scala.math.{Pi, abs, exp, log, sqrt}

// Inspired and heavily used Lichess's implementation of Glicko2 rating
// https://github.com/lichess-org/lila/blob/f70492bd91025a365e9b9903432146c8a5321cb0/modules/rating/src/main/Glicko.scala

final class Glicko2Calculator {

  private val Tau                  = 0.5
  private val Scale                = 173.7178
  private val ConvergenceTolerance = 0.000001

  private def g(phi: Double): Double                                                     = 1.0 / sqrt(1.0 + 3.0 * phi * phi / (Pi * Pi))
  private def expectedScore(mu: Double, opponentMu: Double, opponentPhi: Double): Double = 1.0 / (1.0 + exp(-g(opponentPhi) * (mu - opponentMu)))

  /** Step 1: Increase deviation based on time since last rating period.
    *
    * This does NOT change rating or volatility.
    */
  def applyDecay(player: Rating, periodsMissed: Double): Rating =
    if (periodsMissed <= 0) player
    else {
      val phi    = player.phi
      val newPhi = sqrt(phi * phi + periodsMissed * player.volatility * player.volatility)
      Rating(rating = player.rating, deviation = newPhi * Scale, volatility = player.volatility)
    }

  def calculate(
    player: Rating,
    matchResult: List[MatchResult] // TODO - change this from List to singleton since it's always going to have 1
  ): RatingUpdate = {
    if (matchResult.isEmpty) return RatingUpdate(player, player)

    val before = player
    val mu     = player.mu
    val phi    = player.phi
    val sigma  = player.volatility

    // Step 2:
    // Calculate variance
    val varianceInverse = matchResult.map { matchResult =>
      val opponentPhi = matchResult.opponent.phi
      val expected    = expectedScore(mu, matchResult.opponent.mu, opponentPhi)
      val gPhi        = g(opponentPhi)
      gPhi * gPhi * expected * (1.0 - expected)
    }.sum

    val variance = 1.0 / varianceInverse

    // Step 3:
    // Calculate delta
    val delta = variance * matchResult.map { matchResult =>
      val expected = expectedScore(mu, matchResult.opponent.mu, matchResult.opponent.phi)
      g(matchResult.opponent.phi) * (matchResult.score - expected)
    }.sum

    // Step 4:
    // Determine new volatility
    val a = log(sigma * sigma)

    def f(x: Double): Double = {
      val ex          = exp(x)
      val numerator   = ex * (delta * delta - phi * phi - variance - ex)
      val denominator = 2.0 * math.pow(phi * phi + variance + ex, 2)
      (numerator / denominator) - ((x - a) / (Tau * Tau))
    }

    var A = a
    var B =
      if (delta * delta > phi * phi + variance) { log(delta * delta - phi * phi - variance) }
      else {
        var k = 1
        while (f(a - k * Tau) < 0) k += 1
        a - k * Tau
      }

    var fA = f(A)
    var fB = f(B)

    while (abs(B - A) > ConvergenceTolerance) {
      val C  = A + (A - B) * fA / (fB - fA)
      val fC = f(C)
      if (fC * fB <= 0) {
        A = B
        fA = fB
      } else { fA = fA / 2.0 }
      B = C
      fB = fC
    }

    val newSigma = exp(A / 2.0)

    // Step 5:
    // Update deviation
    val phiStar = sqrt(phi * phi + newSigma * newSigma)
    val newPhi  = 1.0 / sqrt((1.0 / (phiStar * phiStar)) + (1.0 / variance))

    // Step 6:
    // Update rating
    val improvement = matchResult.map { matchResult =>
      val expected = expectedScore(mu, matchResult.opponent.mu, matchResult.opponent.phi)
      g(matchResult.opponent.phi) * (matchResult.score - expected)
    }.sum

    val newMu = mu + newPhi * newPhi * improvement
    val after = Rating(rating = newMu * Scale + 1500.0, deviation = newPhi * Scale, volatility = newSigma)

    RatingUpdate(before = before, after = after)
  }
}
