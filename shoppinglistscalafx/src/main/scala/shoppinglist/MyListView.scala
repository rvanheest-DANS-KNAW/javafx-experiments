package shoppinglist

import javafx.event.EventTarget

import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.observables.JavaFxObservable

import scala.collection.JavaConverters._
import scalafx.scene.Node
import scalafx.scene.control.{ ListCell, ListView }
import scalafx.scene.input.{ MouseEvent => sMouseEvent }

class MyListView extends ListView[String] with Disposable {

  focusTraversable = false

  private val dblClickDisposable = JavaFxObservable.eventsOf(this, sMouseEvent.MouseClicked)
    .map[sMouseEvent](e => new sMouseEvent(e))
    .filter(_.clickCount >= 2)
    .flatMapIterable[ListCell[String]](e => findListCell(e.target).toList.asJava)
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
