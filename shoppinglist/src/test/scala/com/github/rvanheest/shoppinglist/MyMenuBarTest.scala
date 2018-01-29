package com.github.rvanheest.shoppinglist

import javafx.scene.Scene
import javafx.stage.Stage

import org.junit.Test
import org.junit.experimental.categories.Category
import org.testfx.framework.junit.ApplicationTest

@Category(Array(classOf[UITest]))
class MyMenuBarTest extends ApplicationTest {

  private var menuBar: MyMenuBar = _

  override def start(stage: Stage): Unit = {
    menuBar = new MyMenuBar
    val scene = new Scene(menuBar)
    stage.setScene(scene)
    stage.show()
  }

  @Test
  def testAddButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = menuBar.addEvent().test()

    clickOn("#menubar_file")
    clickOn("#menubar_file_add")

    testObserver.assertValueCount(1)
      .assertNoErrors()
      .assertNotComplete()
  }

  @Test
  def testClearButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = menuBar.clearEvent().test()

    clickOn("#menubar_file")
    clickOn("#menubar_file_clear")

    testObserver.assertValueCount(1)
      .assertNoErrors()
      .assertNotComplete()
  }

  @Test
  def testCloseButtonShouldProduceAnEventWhenClicked(): Unit = {
    val testObserver = menuBar.closeEvent().test()

    clickOn("#menubar_file")
    clickOn("#menubar_file_close")

    testObserver.assertValueCount(1)
      .assertNoErrors()
      .assertNotComplete()
  }
}
