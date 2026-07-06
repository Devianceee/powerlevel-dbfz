package rating

import domain.model.{MatchResult, Rating}

import scala.math.{Pi, abs, exp, log, sqrt}

final class GlickoCalculator {
  val Tau                          = 0.5
  private val Scale                = 173.7178
  private val ConvergenceTolerance = 0.000001
  private val RatingPeriodMs       = 60000.0 // 60 seconds

  private def g(phi: Double): Double = 1.0 / sqrt(1.0 + 3.0 * phi * phi / (Pi * Pi))

  private def E(mu: Double, muJ: Double, phiJ: Double): Double =
    1.0 / (1.0 + exp(-g(phiJ) * (mu - muJ)))

  def applyDecay(player: Rating, elapsedMillis: Double): Rating = {
    val periodsMissed = math.max(0.0, elapsedMillis / RatingPeriodMs)
    if (periodsMissed <= 0.0) return player

    val newPhi = sqrt(player.phi * player.phi + periodsMissed * player.vol * player.vol)
    Rating(player.rating, math.min(newPhi * Scale, 350.0), player.vol)
  }

  def calculateNewRating(player: Rating, matches: List[MatchResult]): Rating = {
    if (matches.isEmpty) return player

    val (mu, phi, sigma) = (player.mu, player.phi, player.vol)

    val vInv = matches.map { m =>
      val gPhiJ = g(m.opponent.phi)
      val e     = E(mu, m.opponent.mu, m.opponent.phi)
      gPhiJ * gPhiJ * e * (1.0 - e)
    }.sum
    val v = 1.0 / vInv

    val delta = v * matches.map { m =>
      g(m.opponent.phi) * (m.score - E(mu, m.opponent.mu, m.opponent.phi))
    }.sum

    val a                    = log(sigma * sigma)
    def f(x: Double): Double = {
      val ex  = exp(x)
      val num = ex * (delta * delta - phi * phi - v - ex)
      val den = 2.0 * math.pow(phi * phi + v + ex, 2)
      (num / den) - ((x - a) / (Tau * Tau))
    }

    var A = a
    var B =
      if (delta * delta > phi * phi + v) log(delta * delta - phi * phi - v)
      else {
        var k = 1
        while (f(a - k * Tau) < 0) k += 1
        a - k * Tau
      }

    var fA = f(A); var fB = f(B)

    while (abs(B - A) > ConvergenceTolerance) {
      val C  = A + (A - B) * fA / (fB - fA)
      val fC = f(C)
      if (fC * fB <= 0) { A = B; fA = fB }
      else { fA = fA / 2.0 }
      B = C; fB = fC
    }
    val newSigma = exp(A / 2.0)

    val phiStar = sqrt(phi * phi + newSigma * newSigma)
    val newPhi  = 1.0 / sqrt(1.0 / (phiStar * phiStar) + 1.0 / v)
    val newMu   = mu + newPhi * newPhi * matches.map { m =>
      g(m.opponent.phi) * (m.score - E(mu, m.opponent.mu, m.opponent.phi))
    }.sum

    Rating(newMu * Scale + 1500.0, newPhi * Scale, newSigma)
  }
}
