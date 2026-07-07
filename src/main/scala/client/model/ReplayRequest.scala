package client.model

case class ReplayRequest(limit: Int, character: Int)

object ReplayRequest {
  val default: ReplayRequest = ReplayRequest(limit = 100, character = -1)
}
