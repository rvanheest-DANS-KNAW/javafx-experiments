package com.github.rvanheest.shoppinglist.backend

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FlatSpec, Matchers }
import rx.lang.scala.observers.TestSubscriber

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ShoppingListAPISpec extends FlatSpec with Matchers with ScalaFutures {

  private def expects(api: ShoppingListAPI)(ss: String*): Unit = {
    val testSubscriber = TestSubscriber[Seq[String]]
    api.get.map(_.map(_.text)).subscribe(testSubscriber)
    testSubscriber.assertValue(ss.toSeq)
    testSubscriber.assertNoErrors()
    testSubscriber.assertNotCompleted()
  }

  private def add(api: ShoppingListAPI)(ss: String*): Future[Unit] = {
    ss.foldLeft(Future.unit)((future, s) => future.flatMap(_ => api.add(s)))
  }

  "add" should "put a ShoppingListItem in the list" in {
    val api = new ShoppingListAPI
    whenReady(add(api)("abc")) { _ =>
      expects(api)("abc")
    }
  }

  it should "put another ShoppingListItem in the list" in {
    val api = new ShoppingListAPI
    whenReady(add(api)("abc", "def")) { _ =>
      expects(api)("abc", "def")
    }
  }

  "remove" should "delete the ShoppingListItem with the given index from list" in {
    val api = new ShoppingListAPI
    val response = add(api)("abc", "def").flatMap(_ => api.remove(0))

    whenReady(response) { _ =>
      expects(api)("def")
    }
  }

  it should "not delete an element from the list when the index is negative" in {
    val api = new ShoppingListAPI
    val response = add(api)("abc", "def").flatMap(_ => api.remove(-1))

    whenReady(response.failed) { x =>
      x shouldBe an[ArrayIndexOutOfBoundsException]
      x.getMessage should include ("-1")
      expects(api)("abc", "def")
    }
  }

  it should "not delete an element from the list when the index is larger than the length of the list" in {
    val api = new ShoppingListAPI
    val response = add(api)("abc", "def").flatMap(_ => api.remove(4))

    whenReady(response.failed) { x =>
      x shouldBe an[ArrayIndexOutOfBoundsException]
      x.getMessage should include ("4")
      expects(api)("abc", "def")
    }
  }

  "clear" should "delete all ShoppingListItems from the list" in {
    val api = new ShoppingListAPI
    val response = add(api)("abc", "def").flatMap(_ => api.clear())

    whenReady(response) { _ =>
      expects(api)()
    }
  }

  "get" should "receive the list's states while doing operations on it" in {
    val api = new ShoppingListAPI
    val testSubscriber = TestSubscriber[Seq[String]]
    api.get.map(_.map(_.text)).subscribe(testSubscriber)

    testSubscriber.assertValuesAndClear(Seq.empty)

    whenReady(api.add("abc")) { _ =>
      testSubscriber.assertValuesAndClear(Seq("abc"))
    }

    whenReady(api.add("def").flatMap(_ => api.add("ghi"))) { _ =>
      testSubscriber.assertValuesAndClear(Seq("abc", "def"), Seq("abc", "def", "ghi"))
    }

    whenReady(api.remove(1)) { _ =>
      testSubscriber.assertValuesAndClear(Seq("abc", "ghi"))
    }

    whenReady(api.add("def")) { _ =>
      testSubscriber.assertValuesAndClear(Seq("abc", "ghi", "def"))
    }

    whenReady(api.clear()) { _ =>
      testSubscriber.assertValuesAndClear(Seq.empty)
    }

    testSubscriber.assertNoErrors()
    testSubscriber.assertNotCompleted()
  }
}
