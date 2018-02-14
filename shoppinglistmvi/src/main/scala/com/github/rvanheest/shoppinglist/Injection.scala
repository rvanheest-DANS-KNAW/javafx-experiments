package com.github.rvanheest.shoppinglist

import com.github.rvanheest.shoppinglist.backend.ShoppingListAPI
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.presenter.{ MenuBarPresenter, ShoppingListPresenter }

object Injection {

  private lazy val shoppingListAPI = new ShoppingListAPI

  private def newShoppingListInteractor = new ShoppingListInteractor(shoppingListAPI)

  def newShoppingListPresenter = new ShoppingListPresenter(newShoppingListInteractor)

  def newMenuBarPresenter = new MenuBarPresenter(newShoppingListInteractor)
}
