package service

import client.ReplayClient
import rating.GlickoCalculator
import repository.{PlayerRepository, RatingHistoryRepository, RatingRepository, ReplayRepository}

trait ReplayIngestionService {}

final class ReplayIngestionServiceImpl(
    client: ReplayClient,
    playerRepo: PlayerRepository,
    replayRepo: ReplayRepository,
    ratingRepo: RatingRepository,
    ratingHistoryRepo: RatingHistoryRepository,
    ratingCalculator: GlickoCalculator
) extends ReplayIngestionService
