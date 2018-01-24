package com.github.rvanheest.shoppinglist

import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

import org.junit.Assert._
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.LabeledMatchers

class MyListViewTest extends ApplicationTest {

  private var listView: MyListView = _

  override def start(stage: Stage): Unit = {
    listView = new MyListView {
      getItems.addAll("foo", "bar", "baz", "qux", "quux")
    }
    val scene = new Scene(listView)
    stage.setScene(scene)
    stage.show()
  }

  override def stop(): Unit = {
    listView.dispose()
  }

  @Test
  def testDoubleClickOnElementShouldDeleteIt(): Unit = {
    val label = "bar"

    doubleClickOn(LabeledMatchers.hasText(label))
    assertFalse(listView.getItems.contains(label))
  }
}
