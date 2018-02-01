package com.github.rvanheest.shoppinglist.ui

import javafx.event.ActionEvent
import javafx.scene.control.{ Menu, MenuBar, MenuItem }
import javafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }
import javafx.stage.Window

import com.github.rvanheest.mvi.lib.View
import com.github.rvanheest.shoppinglist.Injection
import com.github.rvanheest.shoppinglist.presenter.MenuBarPresenter
import rx.lang.scala.{ Observable, Subscription }
import rx.lang.scala.JavaConverters._
import rx.observables.JavaFxObservable

trait MenuBarView extends View {
  def addIntent(): Observable[Unit]

  def clearIntent(): Observable[Unit]

  def closeIntent(): Observable[Unit]
}

class MenuBarUI(owner: Window) extends MenuBar with MenuBarView with Subscription {

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
  private val presenter = createPresenter(owner)
  presenter.attachView(this)

  override def addIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(add).asScala.map(_ => ())

  override def clearIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(clear).asScala.map(_ => ())

  override def closeIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(close).asScala.map(_ => ())

  protected def createPresenter(owner: Window): MenuBarPresenter = Injection.newMenuBarPresenter(owner)

  override def isUnsubscribed: Boolean = presenter.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    presenter.unsubscribe()
    super.unsubscribe()
  }
}
