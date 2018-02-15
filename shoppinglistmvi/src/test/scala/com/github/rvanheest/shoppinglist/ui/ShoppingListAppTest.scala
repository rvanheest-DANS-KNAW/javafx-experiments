package com.github.rvanheest.shoppinglist.ui

import java.util.concurrent.TimeUnit
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.stage.Stage

import com.github.rvanheest.shoppinglist.UITest
import org.junit.Assert.{ assertFalse, assertTrue }
import org.junit.Test
import org.junit.experimental.categories.Category
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.LabeledMatchers

@Category(Array(classOf[UITest]))
class ShoppingListAppTest extends ApplicationTest {

  private var app: ShoppingListApp = _

  override def start(stage: Stage): Unit = {
    app = new ShoppingListApp(stage)
    val scene = new Scene(app)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    app.unsubscribe()
    app = null
  }

  private def addItem(text: String): Unit = {
    clickOn("#menubar_file")
    clickOn("#menubar_file_add")
    clickOn(".text-field")
    write(text)
    clickOn(".btn-success")
    sleep(200, TimeUnit.MILLISECONDS)
  }

  @Test
  def testAddItem(): Unit = {
    val listView = lookup("#shoppinglist_ui").query[ListView[String]]()
    val text = "hello world"

    addItem(text)

    assertTrue(listView.getItems.contains(text))
  }

  @Test
  def testClearItems(): Unit = {
    val listView = lookup("#shoppinglist_ui").query[ListView[String]]()
    val text = "foobar"

    addItem(text)

    clickOn("#menubar_file")
    clickOn("#menubar_file_clear")

    assertTrue(listView.getItems.isEmpty)
  }

  @Test
  def testClearItemByDoubleClicking(): Unit = {
    val listView = lookup("#shoppinglist_ui").query[ListView[String]]()
    val text = "abcdef"

    addItem(text)

    assertTrue(listView.getItems.contains(text))

    doubleClickOn(LabeledMatchers.hasText(text))

    assertFalse(listView.getItems.contains(text))
  }
}
