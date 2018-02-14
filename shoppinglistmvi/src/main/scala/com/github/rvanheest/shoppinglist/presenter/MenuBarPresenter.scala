package com.github.rvanheest.shoppinglist.presenter

import javafx.application.Platform

import com.github.rvanheest.mvi.lib.BasePresenter
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ MenuBarView, MenuBarViewState }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Subscription
import rx.schedulers.JavaFxScheduler

class MenuBarPresenter(interactor: ShoppingListInteractor) extends BasePresenter[MenuBarView, MenuBarViewState] {

  private var clearSubscription: Subscription = _
  private var closeSubscription: Subscription = _

  override protected def bindIntents(): Unit = {
    val addBehavior = intent(_.addIntent()).map(_ => MenuBarViewState(true))

    val addedBehavior = for {
      item <- intent(_.addShoppingItemIntent())
      _ <- interactor.add(item)
    } yield MenuBarViewState(false)

    val clearBehavior = for {
      _ <- intent(_.clearIntent())
      _ <- interactor.clear()
    } yield ()

    val closeBehavior = intent(_.closeIntent())

    clearSubscription = clearBehavior.subscribe
    closeSubscription = closeBehavior.subscribe(_ => Platform.exit())

    val viewState = addBehavior.merge(addedBehavior).observeOn(JavaFxScheduler.platform().asScala)
    subscribeViewState(viewState, _ render _)
  }

  override protected def unbindIntents(): Unit = {
    unsubscribe(clearSubscription)
    unsubscribe(closeSubscription)
  }

  private def unsubscribe(subscription: Subscription): Unit = {
    if (subscription != null && !subscription.isUnsubscribed)
      subscription.unsubscribe()
  }
}
