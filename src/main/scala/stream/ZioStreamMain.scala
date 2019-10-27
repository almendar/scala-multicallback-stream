package stream
import zio.App
import zio.ZIO
import zio.Queue
import zio.stream.{ZStream, Sink}
import zio.console
import zio.duration.Duration
import java.util.concurrent.TimeUnit
object ZioStreamMain extends App {

  val h = new Handle

  val multiAsyncRead: ZStream[Any, Throwable, Either[Throwable, Int]] = for {
    q <- ZStream.fromEffect(Queue.unbounded[Either[Throwable, Int]])
    stream <- ZStream.fromEffect {
      ZIO.effect(h.handel(e => unsafeRunAsync_(q.offer(e))))
    }
    el <- ZStream.fromQueueWithShutdown(q)
  } yield el

  def run(args: List[String]): zio.ZIO[zio.ZEnv, Nothing, Int] =
    for {
      fib <- multiAsyncRead
        .tap (x => console.putStrLn(x.toString()))
        .run(Sink.drain)
        .fork
        .orDie
      _ <- ZIO.sleep(Duration.apply(5, TimeUnit.SECONDS)) *> ZIO(h.stop()).orDie

    } yield 0
}
