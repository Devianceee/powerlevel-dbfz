package service

import cats.effect.IO
import cats.syntax.all.toTraverseOps
import client.ReplayClient
import domain.model.database.{DbPlayerRow, DbRatingHistoryInsert, DbRatingRow, DbReplayRow}
import domain.model.{MatchRecord, MatchResult, Player, Rating, ReplayId}
import doobie.{ConnectionIO, FC, Transactor}
import doobie.implicits.*
import org.typelevel.log4cats.Logger
import rating.Glicko2Calculator
import repository.{PlayerRepository, RatingHistoryRepository, RatingRepository, ReplayRepository}

trait IngestService {
  def ingest(limit: Int): IO[Unit]
}

final class IngestServiceImpl(
  xa: Transactor[IO],
  client: ReplayClient,
  playerRepo: PlayerRepository,
  replayRepo: ReplayRepository,
  ratingRepo: RatingRepository,
  ratingHistoryRepo: RatingHistoryRepository,
  ratingCalculator: Glicko2Calculator
)(using logger: Logger[IO])
    extends IngestService {

  override def ingest(limit: Int): IO[Unit] =
    for {
      _        <- logger.info("Getting replays")
      response <- client.getReplays(limit)
      _        <- logger.info("Retrieved replays")
      _        <- response.matches.values.toList.traverse(processMatch) // Go through each replay individually
    } yield ()

  private def processMatch(matchRecord: MatchRecord): IO[Unit] =
    (for {
      _      <- playerRepo.upsert(DbPlayerRow(matchRecord.winningPlayer.playerId, matchRecord.winningPlayer.username))
      _      <- playerRepo.upsert(DbPlayerRow(matchRecord.losingPlayer.playerId, matchRecord.losingPlayer.username))
      exists <- replayRepo.exists(matchRecord.replayId)
      _      <- if (exists) FC.unit else updateRatings(matchRecord)
    } yield ()).transact(xa)

  private def updateRatings(matchRecord: MatchRecord): ConnectionIO[Unit] =
    for {
      _ <- replayRepo.insert(
        DbReplayRow(
          id = matchRecord.replayId,
          winnerId = matchRecord.winningPlayer.playerId,
          loserId = matchRecord.losingPlayer.playerId,
          winnerCharacters = matchRecord.winningCharacters,
          loserCharacters = matchRecord.losingCharacters,
          playedAt = matchRecord.timestamp
        )
      )

      _ <-
        updatePlayerRating(
          player = matchRecord.winningPlayer,
          opponent = matchRecord.losingPlayer,
          score = 1.0,
          matchRecord = matchRecord
        ) // Set rating for winner

      _ <-
        updatePlayerRating(
          player = matchRecord.losingPlayer,
          opponent = matchRecord.winningPlayer,
          score = 0.0,
          matchRecord = matchRecord
        ) // Set rating for loser
    } yield ()

  private def updatePlayerRating(player: Player, opponent: Player, score: Double, matchRecord: MatchRecord): ConnectionIO[Unit] =
    for {
      currentRating  <- ratingRepo.find(player.playerId).map(_.map(_.toGlicko).getOrElse(Rating.default))
      opponentRating <- ratingRepo.find(opponent.playerId).map(_.map(_.toGlicko).getOrElse(Rating.default))

      before = currentRating
      after = ratingCalculator
        .calculate(
          currentRating,
          List( // TODO - change this from List to singleton since it's always going to have 1
            MatchResult(opponent = opponentRating, score = score, timestamp = matchRecord.timestamp)
          )
        )
        .after

      _ <- ratingRepo.upsert(DbRatingRow(playerId = player.playerId, rating = after.rating, deviation = after.deviation, volatility = after.volatility))

      _ <- ratingHistoryRepo.insert(
        DbRatingHistoryInsert(
          replayId = matchRecord.replayId,
          playerId = player.playerId,
          ratingBefore = before.rating,
          ratingAfter = after.rating,
          deviationBefore = before.deviation,
          deviationAfter = after.deviation,
          volatilityBefore = before.volatility,
          volatilityAfter = after.volatility
        )
      )

    } yield ()
}
