package rating

import domain.model.{MatchResult, Rating}

trait RatingCalculator {
  def calculateNewRating(player: Rating, matches: List[MatchResult]): Rating
}
