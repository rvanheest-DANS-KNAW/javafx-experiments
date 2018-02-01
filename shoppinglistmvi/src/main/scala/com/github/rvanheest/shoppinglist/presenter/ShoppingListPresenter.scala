package com.github.rvanheest.shoppinglist.presenter

import com.github.rvanheest.mvi.lib.BasePresenter
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ ShoppingListState, ShoppingListView }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Subscription
import rx.schedulers.JavaFxScheduler

class ShoppingListPresenter(interactor: ShoppingListInteractor) extends BasePresenter[ShoppingListView, ShoppingListState] {

  private var removeItemIntent: Subscription = _

  override protected def bindIntents(): Unit = {
    removeItemIntent = intent(_.doubleClickListItemIntent)
      .flatMap(interactor.remove)
      .subscribe()

    val items = interactor.getItems.observeOn(JavaFxScheduler.platform().asScala)
    subscribeViewState(items, _ render _)
  }

  override protected def unbindIntents(): Unit = {
    removeItemIntent.unsubscribe()
  }
}
