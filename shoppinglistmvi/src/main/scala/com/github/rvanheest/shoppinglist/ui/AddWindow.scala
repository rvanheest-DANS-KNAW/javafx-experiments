package com.github.rvanheest.shoppinglist.ui

import javafx.scene.control.{ ButtonType, Dialog }
import javafx.stage.{ Modality, Window }

class AddWindow(owner: Window) extends Dialog[String] {
  private val addDialog = new AddDialog

  initModality(Modality.APPLICATION_MODAL)
  initOwner(owner)
  setDialogPane(addDialog)
  setResultConverter {
    case ButtonType.APPLY => addDialog.getText
    case ButtonType.CANCEL => null
  }
}
