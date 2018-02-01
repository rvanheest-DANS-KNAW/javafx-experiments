package com.github.rvanheest.shoppinglist.interactor

import com.github.rvanheest.shoppinglist.backend.ShoppingListAPI
import com.github.rvanheest.shoppinglist.ui.ShoppingListState
import rx.lang.scala.{ FutureToObservable, Observable }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ShoppingListInteractor(api: ShoppingListAPI) {

  def add(text: String): Observable[Unit] = api.add(text).toObservable

  def remove(index: Int): Observable[Unit] = api.remove(index).toObservable

  def clear(): Observable[Unit] = api.clear().toObservable

  def getItems: Observable[ShoppingListState] = {
    api.get
      .map(it => ShoppingListState.Result(it.map(_.text)))
      .onErrorReturn(ShoppingListState.Error)
  }
}
