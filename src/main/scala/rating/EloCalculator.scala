package rating

import domain.model.{MatchResult, Rating}

class EloCalculator extends RatingCalculator {
  override def calculateNewRating(player: Rating, matches: List[MatchResult]): Rating = ???
}
