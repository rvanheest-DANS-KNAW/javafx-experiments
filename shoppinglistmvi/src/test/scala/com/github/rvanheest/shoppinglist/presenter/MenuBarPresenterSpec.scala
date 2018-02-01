package com.github.rvanheest.shoppinglist.presenter

import java.util.Optional
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.stage.Window

import com.github.rvanheest.shoppinglist.backend.{ ShoppingListAPI, ShoppingListAPISpec }
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ AddWindow, MenuBarView }
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers }
import rx.lang.scala.Observable

class MenuBarPresenterSpec extends FlatSpec with Matchers with MockFactory with BeforeAndAfterEach {

  // TODO testing doesn't work here yet, since we do UI stuff in the corresponding presenter,
  // which is actually not supposed to happen. I'm however not yet sure where else to put this.

//  class TestInteractor extends ShoppingListInteractor(mock[ShoppingListAPI])
//
//  private val mockInteractor = mock[TestInteractor]
//  private val mockAddWindow = mock[TestAddWindow]
//  private val mockView = mock[MenuBarView]
//  private val addWindowFactory = () => mockAddWindow
//  private val presenter = new MenuBarPresenter(mockInteractor, addWindowFactory)
//
//  override def afterEach(): Unit = {
//    presenter.unsubscribe()
//    super.afterEach()
//  }
//
//  "add" should "" in {
//    mockView.addIntent _ expects() once() returning Observable.just((), (), ())
////    (mockAddWindow.showAndWait _).expects().repeat(3).returning(Optional.of("foo"))
//
//
//  }
}
