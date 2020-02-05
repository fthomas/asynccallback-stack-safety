import cats.Monad
import cats.effect.IO
import cats.implicits._
import japgolly.scalajs.react.AsyncCallback
import japgolly.scalajs.react.CatsReact.reactAsyncCallbackCatsInstance
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global

class StackSafetyTest extends AnyFunSuite {
  def nestedFlatMapsInTailrecLoop[F[_]](implicit F: Monad[F]): F[Int] = {
    val n = 5000
    @scala.annotation.tailrec
    def sum(list: List[F[Int]])(acc: F[Int]): F[Int] =
      list match {
        case Nil          => acc
        case head :: tail => sum(tail)(head.flatMap(h => acc.map(_ + h)))
      }
    sum(List.fill(n)(F.pure(1)))(F.pure(0))
  }

  def nestedFlatMapsInNonTailrecLoop[F[_]](implicit F: Monad[F]): F[Int] = {
    val n = 5000
    def sum(list: List[F[Int]])(acc: F[Int]): F[Int] =
      list match {
        case Nil          => acc
        case head :: tail => head.flatMap(h => sum(tail)(acc.map(_ + h)))
      }
    sum(List.fill(n)(F.pure(1)))(F.pure(0))
  }

  test("AsyncCallback is stack-safe") {
    nestedFlatMapsInTailrecLoop[AsyncCallback].unsafeToFuture()
  }

  test("AsyncCallback trampolines") {
    nestedFlatMapsInNonTailrecLoop[AsyncCallback].unsafeToFuture()
  }

  test("IO is stack-safe") {
    nestedFlatMapsInTailrecLoop[IO].unsafeToFuture().onComplete(println)
  }

  test("IO trampolines") {
    nestedFlatMapsInNonTailrecLoop[IO].unsafeToFuture().onComplete(println)
  }
}
