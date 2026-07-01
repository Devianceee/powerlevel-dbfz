package client.model

import domain.model.{LoginId, SteamId}

case class Login(steamId: SteamId, loginId: LoginId)
