package service

import query.PlayerQueries

trait PlayerService {}

final class PlayerServiceImpl(playerQueries: PlayerQueries) extends PlayerService {}
