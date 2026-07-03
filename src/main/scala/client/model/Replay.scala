package client.model

import domain.model.PlayerId

case class Replay(playerId: PlayerId, limit: Int, character: Int = -1)
