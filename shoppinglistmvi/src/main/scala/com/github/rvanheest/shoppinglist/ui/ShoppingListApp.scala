package com.github.rvanheest.shoppinglist.ui

import javafx.scene.layout.{ BorderPane, VBox }
import javafx.stage.Window

import rx.lang.scala.Subscription

class ShoppingListApp(owner: Window) extends BorderPane with Subscription {

  private val menuBar = new MenuBarUI(owner)
  private val header = new Header("Shopping list")
  private val listView = new ShoppingListUI
  private val workPane = new VBox(5.0, header, listView) {
    getStyleClass.addAll("panel-primary")
  }

  setTop(menuBar)
  setCenter(workPane)

  override def isUnsubscribed: Boolean = {
    menuBar.isUnsubscribed && listView.isUnsubscribed && super.isUnsubscribed
  }

  override def unsubscribe(): Unit = {
    menuBar.unsubscribe()
    listView.unsubscribe()
    super.unsubscribe()
  }
}
