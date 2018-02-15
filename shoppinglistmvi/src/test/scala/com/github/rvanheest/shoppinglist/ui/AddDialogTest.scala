package com.github.rvanheest.shoppinglist.ui

import javafx.scene.Scene
import javafx.stage.Stage

import com.github.rvanheest.shoppinglist.UITest
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import org.junit.Assert.assertEquals
import org.junit.experimental.categories.Category

@Category(Array(classOf[UITest]))
class AddDialogTest extends ApplicationTest {

  private var dialog: AddDialog = _

  override def start(stage: Stage): Unit = {
    dialog = new AddDialog
    val scene = new Scene(dialog)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    dialog = null
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
