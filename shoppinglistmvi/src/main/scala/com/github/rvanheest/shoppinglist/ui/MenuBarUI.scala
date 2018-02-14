package com.github.rvanheest.shoppinglist.ui

import javafx.scene.control.{Menu, MenuBar, MenuItem}
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import javafx.stage.Window

import com.github.rvanheest.mvi.lib.View
import com.github.rvanheest.shoppinglist.Injection
import com.github.rvanheest.shoppinglist.presenter.MenuBarPresenter
import rx.lang.scala.JavaConverters._
import rx.lang.scala.subjects.PublishSubject
import rx.lang.scala.{Observable, Subscription}
import rx.observables.JavaFxObservable

case class MenuBarViewState(showAddWindow: Boolean)

trait MenuBarView extends View {
  def addIntent(): Observable[Unit]

  def addShoppingItemIntent(): Observable[String]

  def clearIntent(): Observable[Unit]

  def closeIntent(): Observable[Unit]

  def render(viewState: MenuBarViewState): Unit
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

  private var addWindow: AddWindow = _
  private val addWindowReceivedElements = PublishSubject.apply[String]()

  private val presenter = createPresenter
  presenter.attachView(this)

  override def addIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(add).asScala.map(_ => ())

  override def addShoppingItemIntent(): Observable[String] = addWindowReceivedElements

  override def clearIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(clear).asScala.map(_ => ())

  override def closeIntent(): Observable[Unit] = JavaFxObservable.actionEventsOf(close).asScala.map(_ => ())

  def render(viewState: MenuBarViewState): Unit = {
    if (addWindow == null && viewState.showAddWindow) {
      addWindow = newAddWindow()
      JavaFxObservable.fromDialog(addWindow).asScala.foreach(addWindowReceivedElements.onNext)
      // unsubscribing will be done automatically by fromDialog
    }
    else if (addWindow != null && !viewState.showAddWindow) {
      addWindow = null
    }
  }

  private def newAddWindow() = new AddWindow(owner)

  protected def createPresenter: MenuBarPresenter = Injection.newMenuBarPresenter

  override def isUnsubscribed: Boolean = presenter.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    presenter.unsubscribe()
    super.unsubscribe()
  }
}
