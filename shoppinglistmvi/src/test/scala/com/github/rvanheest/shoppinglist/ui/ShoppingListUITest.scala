package com.github.rvanheest.shoppinglist.ui

import java.util.concurrent.TimeUnit
import javafx.scene.Scene
import javafx.stage.Stage

import com.github.rvanheest.shoppinglist.presenter.ShoppingListPresenter
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.{LabeledMatchers, ListViewMatchers}
import rx.lang.scala.observers.TestSubscriber

@RunWith(classOf[MockitoJUnitRunner])
class ShoppingListUITest extends ApplicationTest {

  @Mock private var presenter: ShoppingListPresenter = _

  class TestShoppingListUI extends ShoppingListUI {
    override protected def createPresenter(): ShoppingListPresenter = presenter
  }

  private var shoppingList: ShoppingListUI = _

  override def start(stage: Stage): Unit = {
    shoppingList = new TestShoppingListUI
    val scene = new Scene(shoppingList)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    shoppingList.unsubscribe()
    shoppingList = null
  }

  @Test
  def testRenderResults(): Unit = {
    val state = ShoppingListState.Result(Seq("foo", "bar", "baz", "qux", "quux"))
    shoppingList.render(state)

    verifyThat(lookup("#shoppinglist_ui"), ListViewMatchers.hasItems(5))
  }

  @Test
  def testRenderMultipleResults(): Unit = {
    val state = ShoppingListState.Result(Seq("foo", "bar", "baz", "qux", "quux"))
    shoppingList.render(state)

    val state2 = ShoppingListState.Result(Seq("abc", "def"))
    shoppingList.render(state2)

    verifyThat(lookup("#shoppinglist_ui"), ListViewMatchers.hasItems(2))
  }

  @Test
  def testDoubleClickOnListItem(): Unit = {
    val testObserver = TestSubscriber[Int]()
    shoppingList.doubleClickListItemIntent.subscribe(testObserver)

    val state = ShoppingListState.Result(Seq("foo", "bar", "baz", "qux", "quux"))
    shoppingList.render(state)

    sleep(200, TimeUnit.MILLISECONDS)
    doubleClickOn(LabeledMatchers.hasText("bar"))

    testObserver.assertValue(1)
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }

  @Test
  def testSingleClickOnListItem(): Unit = {
    val testObserver = TestSubscriber[Int]()
    shoppingList.doubleClickListItemIntent.subscribe(testObserver)

    val state = ShoppingListState.Result(Seq("foo", "bar", "baz", "qux", "quux"))
    shoppingList.render(state)

    sleep(200, TimeUnit.MILLISECONDS)
    clickOn(LabeledMatchers.hasText("bar"))

    testObserver.assertNoValues()
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }
}
