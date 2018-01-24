package shoppinglist

import io.reactivex.Observable
import io.reactivex.rxjavafx.observables.JavaFxObservable

import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control.{ Menu, MenuBar, MenuItem }
import scalafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }

class MyMenuBar extends MenuBar {

  private val add = new MenuItem("Add") {
    accelerator = new KeyCodeCombination(KeyCode.A, KeyCombination.ShortcutDown)
  }
  private val clear = new MenuItem("Clear")
  private val close = new MenuItem("Close") {
    accelerator = new KeyCodeCombination(KeyCode.C, KeyCombination.ShortcutDown)
    onAction = _ => Platform.exit()
  }

  menus = Seq(
    new Menu("File") {
      items = Seq(add, clear, close)
    }
  )

  // TODO implicit conversion JavaFX ActionEvent to ScalaFX ActionEvent
  def addEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(add).map(new ActionEvent(_))

  def clearEvent(): Observable[ActionEvent] = JavaFxObservable.actionEventsOf(clear).map(new ActionEvent(_))
}
