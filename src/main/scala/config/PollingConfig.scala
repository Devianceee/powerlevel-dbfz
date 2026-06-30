package config

import ciris._

case class PollingConfig(pollingInterval: Int)

object PollingConfig {
  def read: ConfigValue[Effect, PollingConfig] = {
    val pollingInterval: ConfigValue[Effect, Int] =
      env("POLLING_INTERVAL").as[Int].option.map(_.getOrElse(60))

    pollingInterval.map(PollingConfig(_))
  }
}
