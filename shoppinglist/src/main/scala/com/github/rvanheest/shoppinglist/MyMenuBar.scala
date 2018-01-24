package com.github.rvanheest.shoppinglist

import javafx.event.ActionEvent
import javafx.scene.control.{ Menu, MenuBar, MenuItem }
import javafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }

import io.reactivex.Observable
import io.reactivex.rxjavafx.observables.JavaFxObservable

class MyMenuBar extends MenuBar {

  private val add = new MenuItem("Add") {
    setId("menubar_file_add")
    setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN))
  }
  private val clear = new MenuItem("Clear") {
    setId("menubar_file_clear")
  }
  private val close = new MenuItem("Close") {
    setId("menubar_file_close")
    setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN))
  }

  getMenus.addAll(
    new Menu("File", null, add, clear, close) {
      setId("menubar_file")
    }
  )

  def addEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(add)

  def clearEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(clear)

  def closeEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(close)
}
