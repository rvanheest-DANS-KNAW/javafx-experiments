package com.github.rvanheest.shoppinglist

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.{ ListCell, ListView }
import javafx.scene.input.MouseEvent

import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.observables.JavaFxObservable

import scala.collection.JavaConverters._

class MyListView extends ListView[String] with Disposable {
  setFocusTraversable(false)

  private val dblClickDisposable = JavaFxObservable.eventsOf(this, MouseEvent.MOUSE_CLICKED)
    .filter(_.getClickCount >= 2)
    .flatMapIterable[ListCell[String]](e => findListCell(e.getTarget).toList.asJava)
    .filter(e => !e.getItem.isEmpty)
    .map[Int](_.getIndex)
    .subscribe(i => this.getItems.remove(i))

  private def findListCell(target: EventTarget): Option[ListCell[String]] = {
    target match {
      case lc: ListCell[String] => Some(lc)
      case t: Node => findListCell(t.getParent)
      case _ => None
    }
  }

  def isDisposed: Boolean = dblClickDisposable.isDisposed

  def dispose(): Unit = dblClickDisposable.dispose()
}
