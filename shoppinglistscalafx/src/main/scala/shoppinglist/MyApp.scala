package shoppinglist

import io.reactivex.disposables.{ CompositeDisposable, Disposable }
import io.reactivex.rxjavafx.observables.JavaFxObservable

import scalafx.scene.layout.{ BorderPane, VBox }

class MyApp(addWindow: () => AddWindow) extends BorderPane with Disposable {

  private val menuBar = new MyMenuBar

  private val header = new MyHeader("Shopping List")
  private val listView = new MyListView

  private val workPane = new VBox(5.0, header, listView) {
    styleClass += "panel-primary"
  }

  top = menuBar
  center = workPane

  private val addItemDisposable = menuBar.addEvent()
    .flatMapMaybe(_ => JavaFxObservable.fromDialog(addWindow()))
    .subscribe(item => listView.getItems.add(item)) // TODO how to use the scalafx API here?

  private val clearItemsDisposable = menuBar.clearEvent().subscribe(_ => listView.getItems.clear()) // TODO how to use the scalafx API here?

  private val compositeDisposable = new CompositeDisposable(addItemDisposable, clearItemsDisposable)

  def isDisposed: Boolean = compositeDisposable.isDisposed

  def dispose(): Unit = compositeDisposable.dispose()
}
