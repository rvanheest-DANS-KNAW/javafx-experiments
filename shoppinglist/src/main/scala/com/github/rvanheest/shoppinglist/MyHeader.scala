package com.github.rvanheest.shoppinglist

import javafx.scene.control.Label
import javafx.scene.layout.HBox

class MyHeader(text: String) extends HBox {
  getChildren.addAll(new Label(text) {
    getStyleClass.addAll("panel-title")
  })
  getStyleClass.addAll("panel-heading", "center")
}
