package scheduler

import cats.effect.IO

trait ReplayPolling {
  def start(): IO[Unit]
}

final class ReplayPollingImpl extends ReplayPolling {
  override def start(): IO[Unit] = ???
}
