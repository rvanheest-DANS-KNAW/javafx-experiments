package com.github.rvanheest.shoppinglist

import javafx.application.Platform
import javafx.scene.layout.{ BorderPane, VBox }

import io.reactivex.disposables.{ CompositeDisposable, Disposable }
import io.reactivex.rxjavafx.observables.JavaFxObservable

class MyApp(addWindow: () => AddWindow) extends BorderPane with Disposable {

  private val menuBar = new MyMenuBar

  private val header = new MyHeader("Shopping List")
  private val listView = new MyListView

  private val workPane = new VBox(5.0, header, listView) {
    getStyleClass.addAll("panel-primary")
  }

  setTop(menuBar)
  setCenter(workPane)

  private val addItemDisposable = menuBar.addEvent()
    .flatMapMaybe(_ => JavaFxObservable.fromDialog(addWindow()))
    .subscribe(item => listView.getItems.add(item))

  private val clearItemsDisposable = menuBar.clearEvent().subscribe(_ => listView.getItems.clear())

  private val closeDisposable = menuBar.closeEvent().subscribe(_ => Platform.exit())

  private val compositeDisposable = new CompositeDisposable(addItemDisposable, clearItemsDisposable, closeDisposable)

  def isDisposed: Boolean = compositeDisposable.isDisposed

  def dispose(): Unit = compositeDisposable.dispose()
}
