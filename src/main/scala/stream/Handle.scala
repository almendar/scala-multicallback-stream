package stream
import java.util.concurrent.atomic.AtomicBoolean

class Handle {

  private var i = 0
  private val shouldRun = new AtomicBoolean(true)

  def handel(cb: Either[Throwable, Int] => Unit): Unit = {
    new Thread(() => {
      while (shouldRun.get) {
        Thread.sleep(400)
        i += 1
        cb(Right(i))
      }
    }).start()
    
  }

  def stop(): Unit = shouldRun.set(false)
}