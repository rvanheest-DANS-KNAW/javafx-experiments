package com.github.rvanheest.shoppinglist.backend

import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

import scala.concurrent.Future

class ShoppingListAPI {

  private val shoppingList: BehaviorSubject[ShoppingList] = BehaviorSubject(Seq.empty)

  def add(text: String): Future[Unit] = {
    val list = shoppingList.toBlocking.head
    val itemToAdd = ShoppingListItem(text = text)
    val newList = list :+ itemToAdd
    shoppingList.onNext(newList)
    // no Completable in RxScala 1.x yet, so Future[Unit] will do
    Future.unit
  }

  def remove(index: Int): Future[Unit] = {
    if (index < 0)
      return Future.failed(new ArrayIndexOutOfBoundsException(index))

    val list = shoppingList.toBlocking.head

    if (index >= list.size)
      return Future.failed(new ArrayIndexOutOfBoundsException(index))

    val newList = list.patch(index, Nil, 1)
    shoppingList.onNext(newList)
    // no Completable in RxScala 1.x yet, so Future[Unit] will do
    Future.unit
  }

  def clear(): Future[Unit] = {
    shoppingList.onNext(List.empty)
    // no Completable in RxScala 1.x yet, so Future[Unit] will do
    Future.unit
  }

  def get: Observable[ShoppingList] = shoppingList
}
