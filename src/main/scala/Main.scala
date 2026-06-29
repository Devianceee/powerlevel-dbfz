import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    App.resource
      .evalTap(_ => IO.println("Starting powerlevel-dbfz..."))
      .useForever
      .as(ExitCode.Success)
}
