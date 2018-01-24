package com.github.rvanheest.shoppinglist

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.control.{ Menu, MenuBar, MenuItem }
import javafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }

import io.reactivex.Observable
import io.reactivex.rxjavafx.observables.JavaFxObservable

class MyMenuBar extends MenuBar {

  private val add = new MenuItem("Add") {
    setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN))
  }
  private val clear = new MenuItem("Clear")
  private val close = new MenuItem("Close") {
    setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN))
    setOnAction(_ => Platform.exit())
  }

  getMenus.addAll(
    new Menu("File", null, add, clear, close)
  )

  def addEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(add)

  def clearEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(clear)
}
