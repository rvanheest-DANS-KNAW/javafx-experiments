package com.github.rvanheest.shoppinglist.interactor

import com.github.rvanheest.shoppinglist.backend.{ ShoppingListAPI, ShoppingListItem }
import com.github.rvanheest.shoppinglist.ui.ShoppingListState
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpec, Matchers }
import rx.lang.scala.Observable
import rx.lang.scala.observers.TestSubscriber

import scala.concurrent.Future

class ShoppingListInteractorSpec extends FlatSpec with Matchers with MockFactory {

  private val api = mock[ShoppingListAPI]
  private val interactor = new ShoppingListInteractor(api)

  "add" should "call the backend API and stream the result back" in {
    api.add _ expects "abc" once() returning Future.unit
    val testSubscriber = TestSubscriber[Unit]
    interactor.add("abc").subscribe(testSubscriber)

    testSubscriber.awaitTerminalEvent()
    testSubscriber.assertValueCount(1)
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }

  "remove" should "call the backend API and stream the result back" in {
    api.remove _ expects 1 once() returning Future.unit
    val testSubscriber = TestSubscriber[Unit]
    interactor.remove(1).subscribe(testSubscriber)

    testSubscriber.awaitTerminalEvent()
    testSubscriber.assertValueCount(1)
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }

  "clear" should "call the backend API and stream the result back" in {
    api.clear _ expects () once() returning Future.unit
    val testSubscriber = TestSubscriber[Unit]
    interactor.clear().subscribe(testSubscriber)

    testSubscriber.awaitTerminalEvent()
    testSubscriber.assertValueCount(1)
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }

  "getItems" should "call the backend API and transform the resulting lists to UI models" in {
    val list1 :: list2 :: list3 :: Nil = List(
      Seq(ShoppingListItem(text = "abc")),
      Seq(ShoppingListItem(text = "abc"), ShoppingListItem(text = "def")),
      Seq.empty
    )
    api.get _ expects () once() returning Observable.just(list1, list2, list3)

    val testSubscriber = TestSubscriber[ShoppingListState]
    interactor.getItems.subscribe(testSubscriber)

    testSubscriber.assertValues(
      ShoppingListState.Result(List("abc")),
      ShoppingListState.Result(List("abc", "def")),
      ShoppingListState.Result(List.empty)
    )
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }

  it should "convert errors to an Error viewstate" in {
    val list = Seq(ShoppingListItem(text = "abc"))
    val error = new Exception("foo")
    api.get _ expects () once() returning Observable.just(list) ++ Observable.error(error)

    val testSubscriber = TestSubscriber[ShoppingListState]
    interactor.getItems.subscribe(testSubscriber)

    testSubscriber.assertValues(
      ShoppingListState.Result(List("abc")),
      ShoppingListState.Error(error)
    )
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }
}
