package domain.model

import java.time.OffsetDateTime

final case class RatingPoint(replayId: ReplayId, rating: Double, timestamp: OffsetDateTime)
