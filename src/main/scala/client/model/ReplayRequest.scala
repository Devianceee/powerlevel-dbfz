package client.model

case class ReplayRequest(limit: Int, character: Int)

object ReplayRequest {
  val default: ReplayRequest = ReplayRequest(limit = 999, character = -1)
}
