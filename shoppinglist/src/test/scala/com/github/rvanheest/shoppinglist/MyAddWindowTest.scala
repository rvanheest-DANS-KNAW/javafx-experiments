package com.github.rvanheest.shoppinglist

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import org.junit.Assert._
import org.junit.Test
import org.junit.experimental.categories.Category
import org.testfx.framework.junit.ApplicationTest

@Category(Array(classOf[UITest]))
class MyAddWindowTest extends ApplicationTest {

  private var dialog: AddWindow = _

  override def start(stage: Stage): Unit = {
    val scene = new Scene(new StackPane())
    stage.setScene(scene)
    stage.show()

    dialog = new AddWindow(stage)
    dialog.show()
  }

  override def stop(): Unit = {
    dialog.close()
  }

  @Test
  def testTypeTextAndApply(): Unit = {
    val text = "hello world"

    clickOn(".text-field")
    write(text)
    clickOn(".btn-success")

    Platform.runLater(() => assertEquals(text, dialog.showAndWait().get()))
  }

  @Test
  def testTypeTextAndCancel(): Unit = {
    val text = "hello world"

    clickOn(".text-field")
    write(text)
    clickOn(".btn-default")

    Platform.runLater(() => assertFalse(text, dialog.showAndWait().isPresent))
  }
}
