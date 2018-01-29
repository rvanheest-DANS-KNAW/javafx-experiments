package com.github.rvanheest.shoppinglist

import javafx.scene.Scene
import javafx.stage.Stage

import org.junit.Assert._
import org.junit.Test
import org.junit.experimental.categories.Category
import org.testfx.framework.junit.ApplicationTest

@Category(Array(classOf[UITest]))
class MyAddDialogTest extends ApplicationTest {

  private var dialog: MyAddDialog = _

  override def start(stage: Stage): Unit = {
    dialog = new MyAddDialog
    val scene = new Scene(dialog)
    stage.setScene(scene)
    stage.show()
  }

  @Test
  def testTypeText(): Unit = {
    val text = "hello world"

    clickOn(".text-field")
    write(text)
    clickOn(".btn-success")

    assertEquals(text, dialog.getText)
  }
}
