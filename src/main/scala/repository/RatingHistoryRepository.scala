package repository

import domain.model.database.DbRatingHistoryInsert
import doobie.ConnectionIO
import doobie.implicits.*

trait RatingHistoryRepository {
  def insert(ratingHistory: DbRatingHistoryInsert): ConnectionIO[Int]
}

final class DoobieRatingHistoryRepository extends RatingHistoryRepository {
  override def insert(ratingHistory: DbRatingHistoryInsert): ConnectionIO[Int] =
    sql"""
          INSERT INTO rating_history (
            replay_id,
            player_id,
    
            rating_before,
            rating_after,
    
            deviation_before,
            deviation_after,
    
            volatility_before,
            volatility_after
          )
          VALUES (
            ${ratingHistory.replayId.value},
            ${ratingHistory.playerId.value},
    
            ${ratingHistory.ratingBefore},
            ${ratingHistory.ratingAfter},
    
            ${ratingHistory.deviationBefore},
            ${ratingHistory.deviationAfter},
    
            ${ratingHistory.volatilityBefore},
            ${ratingHistory.volatilityAfter}
          )
        """.update.run
}
