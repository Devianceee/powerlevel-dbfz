package client.model

import domain.model.{AuthToken, PlayerId}

case class LoginResponse(authToken: AuthToken, playerId: PlayerId)
