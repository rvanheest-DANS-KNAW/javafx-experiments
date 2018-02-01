package com.github.rvanheest.shoppinglist

import javafx.stage.Window

import com.github.rvanheest.shoppinglist.backend.ShoppingListAPI
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.presenter.{ MenuBarPresenter, ShoppingListPresenter }
import com.github.rvanheest.shoppinglist.ui.AddWindow

object Injection {

  private lazy val shoppingListAPI = new ShoppingListAPI

  private def newAddWindow(owner: Window) = () => new AddWindow(owner)

  private def newShoppingListInteractor = new ShoppingListInteractor(shoppingListAPI)

  def newShoppingListPresenter = new ShoppingListPresenter(newShoppingListInteractor)

  def newMenuBarPresenter(owner: Window): MenuBarPresenter = new MenuBarPresenter(newShoppingListInteractor, newAddWindow(owner))
}
