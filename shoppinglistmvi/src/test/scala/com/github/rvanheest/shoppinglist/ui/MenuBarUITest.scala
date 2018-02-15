package com.github.rvanheest.shoppinglist.ui

import java.util.concurrent.TimeUnit
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.{ Stage, Window }

import com.github.rvanheest.shoppinglist.UITest
import com.github.rvanheest.shoppinglist.presenter.MenuBarPresenter
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.testfx.framework.junit.ApplicationTest
import rx.lang.scala.observers.TestSubscriber

@Category(Array(classOf[UITest]))
@RunWith(classOf[MockitoJUnitRunner])
class MenuBarUITest extends ApplicationTest {

  @Mock private var presenter: MenuBarPresenter = _

  class TestMenuBar(owner: Window) extends MenuBarUI(owner) {
    override protected def createPresenter: MenuBarPresenter = presenter
  }

  private var menuBar: MenuBarUI = _

  override def start(stage: Stage): Unit = {
    menuBar = new TestMenuBar(stage)
    val scene = new Scene(menuBar)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    menuBar.unsubscribe()
    menuBar = null
  }

  @Test
  def testAddButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = TestSubscriber[Unit]()
    menuBar.addIntent().subscribe(testObserver)

    clickOn("#menubar_file")
    clickOn("#menubar_file_add")

    testObserver.assertValueCount(1)
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }

  @Test
  def testClearButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = TestSubscriber[Unit]()
    menuBar.clearIntent().subscribe(testObserver)

    clickOn("#menubar_file")
    clickOn("#menubar_file_clear")

    testObserver.assertValueCount(1)
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }

  @Test
  def testCloseButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = TestSubscriber[Unit]()
    menuBar.closeIntent().subscribe(testObserver)

    clickOn("#menubar_file")
    clickOn("#menubar_file_close")

    testObserver.assertValueCount(1)
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }

  @Test
  def testRenderAddWindow(): Unit = {
    val testObserver = TestSubscriber[String]()
    menuBar.addShoppingItemIntent().subscribe(testObserver)
    Platform.runLater(() => menuBar.render(MenuBarViewState(true)))

    val text = "hello world"

    sleep(200, TimeUnit.MILLISECONDS)
    clickOn(".text-field")
    write(text)
    clickOn(".btn-success")

    testObserver.assertValue(text)
    testObserver.assertNoErrors()
    testObserver.assertNotCompleted()
  }

  @Test
  def testRenderViewStateFalse(): Unit = {
    testRenderAddWindow()
    menuBar.render(MenuBarViewState(false))
  }
}
