package stream

import fs2._
import fs2.concurrent._
import cats.effect.{IOApp, ExitCode, IO}

/*

def rows[F[_]](h: CSVHandle)(implicit F: ConcurrentEffect[F], cs: ContextShift[F]): Stream[F,Row] =
  for {
    q <- Stream.eval(Queue.unbounded[F,Either[Throwable,Row]])
    _ <-  Stream.eval { F.delay(h.withRows(e => F.runAsync(q.enqueue1(e))(_ => IO.unit).unsafeRunSync)) }
    row <- q.dequeue.rethrow
  } yield row
 */
import scala.concurrent.duration._
object Fs2Main extends IOApp {

  type AsyncInt = Either[Throwable, Int]

  val h = new Handle

  val asyncRead = IO.async[Int] { cb =>
    h.handel(cb)
  }

  def multiAsyncRead: Stream[IO, Int] =
    for {
      q <- Stream.eval(Queue.unbounded[IO, AsyncInt])
      _ <- Stream.eval {
        IO.delay(
          h.handel(
            e =>
              q.enqueue1(e).unsafeRunAsync { cb: Either[Throwable, Unit] =>
                ()
              }
          )
        )
      }
      el <- q.dequeue.rethrow
    } yield el

  def putStrLn[A](a: A) = IO.delay(println(a.toString()))

  def run(args: List[String]): IO[ExitCode] = {
    for {
      fiber1 <- multiAsyncRead.evalTap(x => putStrLn(x)).compile.drain.start
      _ <- IO.sleep(5.seconds).flatMap(_ => IO.delay(h.stop()))
    } yield ()
  }.map(_ => ExitCode.Success)

}
