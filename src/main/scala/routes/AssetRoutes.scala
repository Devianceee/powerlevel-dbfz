package routes

import cats.effect.IO
import org.http4s._
import org.http4s.server.staticcontent._

object AssetRoutes {

  val routes: HttpRoutes[IO] =
    ResourceServiceBuilder[IO]("/assets")
      .withPathPrefix("/assets")
      .toRoutes
}