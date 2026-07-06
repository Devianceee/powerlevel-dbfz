package routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityEncoder._

object HealthRoutes {
  val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "health" =>
      Ok(
        Map(
          "status" -> "ok"
        )
      )
    }
}
