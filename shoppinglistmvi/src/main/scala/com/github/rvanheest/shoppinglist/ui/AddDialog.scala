package com.github.rvanheest.shoppinglist.ui

import javafx.scene.Node
import javafx.scene.control.{ Button, ButtonType, DialogPane, TextField }
import javafx.scene.layout.VBox

class AddDialog extends DialogPane {

  getButtonTypes.addAll(ButtonType.APPLY, ButtonType.CANCEL)

  private val textfield = new TextField {
    setOnAction(_ => lookupButton(ButtonType.APPLY).asInstanceOf[Button].fire())
  }
  setContent(new VBox(10.0, new Header("Enter Item"), textfield))

  def getText: String = textfield.getText

  override protected def createButton(buttonType: ButtonType): Node = {
    val button = super.createButton(buttonType)
    buttonType match {
      case ButtonType.APPLY => button.getStyleClass.addAll("btn", "btn-success")
      case ButtonType.CANCEL => button.getStyleClass.addAll("btn", "btn-default")
    }
    button
  }
}
