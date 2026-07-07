package domain.model

import doobie.Meta

import scala.annotation.nowarn

opaque type ReplayId = Long

object ReplayId {
  def apply(value: Long): ReplayId = value

  extension (id: ReplayId) {
    def value: Long = id
  }

  @nowarn("msg=Infinite loop in function body")
  given Meta[ReplayId] = Meta[Long].imap[ReplayId](value => ReplayId(value))(id => id)
}
