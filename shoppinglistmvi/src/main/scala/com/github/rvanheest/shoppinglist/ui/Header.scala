package com.github.rvanheest.shoppinglist.ui

import javafx.scene.control.Label
import javafx.scene.layout.HBox

class Header(text: String) extends HBox {
  getChildren.addAll(new Label(text) {
    getStyleClass.addAll("panel-title")
  })
  getStyleClass.addAll("panel-heading", "center")
}
