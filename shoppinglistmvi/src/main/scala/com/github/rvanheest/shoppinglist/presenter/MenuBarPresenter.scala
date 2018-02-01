package com.github.rvanheest.shoppinglist.presenter

import javafx.application.Platform

import com.github.rvanheest.mvi.lib.BasePresenter
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ AddWindow, MenuBarView }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Subscription
import rx.observables.JavaFxObservable

class MenuBarPresenter(interactor: ShoppingListInteractor, addWindowFactory: () => AddWindow) extends BasePresenter[MenuBarView, Int] {

  private var addSubscription: Subscription = _
  private var clearSubscription: Subscription = _
  private var closeSubscription: Subscription = _

  override protected def bindIntents(): Unit = {
    val addBehavior = for {
      _ <- intent(_.addIntent())
      item <- JavaFxObservable.fromDialog(addWindowFactory()).asScala
      _ <- interactor.add(item)
    } yield ()

    val clearBehavior = for {
      _ <- intent(_.clearIntent())
      _ <- interactor.clear()
    } yield ()

    val closeBehavior = intent(_.closeIntent())

    addSubscription = addBehavior.subscribe
    clearSubscription = clearBehavior.subscribe
    closeSubscription = closeBehavior.subscribe(_ => Platform.exit())
  }

  override protected def unbindIntents(): Unit = {
    unsubscribe(addSubscription)
    unsubscribe(clearSubscription)
    unsubscribe(closeSubscription)
  }

  private def unsubscribe(subscription: Subscription): Unit = {
    if (subscription != null && !subscription.isUnsubscribed)
      subscription.unsubscribe()
  }
}
