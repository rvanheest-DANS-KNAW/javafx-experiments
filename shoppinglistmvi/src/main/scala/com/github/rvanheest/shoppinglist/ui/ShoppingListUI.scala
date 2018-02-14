package com.github.rvanheest.shoppinglist.ui

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.{ListCell, ListView}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane

import com.github.rvanheest.mvi.lib.View
import com.github.rvanheest.shoppinglist.Injection
import com.github.rvanheest.shoppinglist.presenter.ShoppingListPresenter
import rx.lang.scala.JavaConverters._
import rx.lang.scala.{Observable, OptionToObservable, Subscription}
import rx.observables.JavaFxObservable

import scala.collection.JavaConverters._

sealed abstract class ShoppingListState

object ShoppingListState {
  case class Result(items: Seq[String]) extends ShoppingListState
  case class Error(e: Throwable) extends ShoppingListState
}

trait ShoppingListView extends View {

  def doubleClickListItemIntent: Observable[Int]

  def render(state: ShoppingListState): Unit
}

class ShoppingListUI extends StackPane with ShoppingListView with Subscription {

  private val listView = new ListView[String] {
    setId("shoppinglist_ui")
    setFocusTraversable(false)
  }
  this.getChildren.add(listView)
  private val presenter = createPresenter()
  presenter.attachView(this)

  def doubleClickListItemIntent: Observable[Int] = {
    for {
      clickEvent <- JavaFxObservable.eventsOf(this.listView, MouseEvent.MOUSE_CLICKED).asScala
      if clickEvent.getClickCount >= 2
      target = clickEvent.getTarget
      cell <- findListCell(target).toObservable
      if !cell.getItem.isEmpty
    } yield cell.getIndex
  }

  private def findListCell(target: EventTarget): Option[ListCell[String]] = {
    target match {
      case lc: ListCell[String] => Some(lc)
      case t: Node => findListCell(t.getParent)
      case _ => None
    }
  }

  def render(state: ShoppingListState): Unit = state match {
    case ShoppingListState.Result(items) =>
      // TODO make a more optimal algorithm?
      this.listView.getItems.clear()
      this.listView.getItems.addAll(items.asJava)
    case ShoppingListState.Error(e) =>
      println(s"an error occured: $e")
  }

  protected def createPresenter(): ShoppingListPresenter = Injection.newShoppingListPresenter

  override def isUnsubscribed: Boolean = presenter.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    presenter.unsubscribe()
    super.unsubscribe()
  }
}
