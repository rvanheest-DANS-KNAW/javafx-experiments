package com.github.rvanheest.shoppinglist

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import org.junit.Assert._
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.ListViewMatchers

class MyAppTest extends ApplicationTest {

  private var app: MyApp = _

  override def start(stage: Stage): Unit = {
    app = new MyApp(() => new AddWindow(stage))
    val scene = new Scene(app)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    app.dispose()
  }

  @Test
  def testAddItem(): Unit = {
    val listView = lookup(ListViewMatchers.isEmpty).query[ListView[String]]()
    val text = "hello world"

    clickOn("#menubar_file")
    clickOn("#menubar_file_add")
    clickOn(".text-field")
    write(text)
    clickOn(".btn-success")

    assertTrue(listView.getItems.contains(text))
  }

  @Test
  def testClearItems(): Unit = {
    val listView = lookup(ListViewMatchers.isEmpty).query[ListView[String]]()
    testAddItem()

    clickOn("#menubar_file")
    clickOn("#menubar_file_clear")

    assertTrue(listView.getItems.isEmpty)
  }
}
