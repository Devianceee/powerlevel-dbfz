package service

import client.ReplayClient
import repository.{PlayerRepository, RatingRepository, ReplayRepository}

trait ReplayService {}

final class ReplayServiceImpl(client: ReplayClient, replayRepo: ReplayRepository, playerRepo: PlayerRepository, ratingRepo: RatingRepository)
    extends ReplayService {}
