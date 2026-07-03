package client.model

import domain.model.PlayerId

case class ReplayRequest(limit: Int = 100, character: Int = -1)
