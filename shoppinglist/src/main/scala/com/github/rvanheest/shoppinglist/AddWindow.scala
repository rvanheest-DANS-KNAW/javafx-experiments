package com.github.rvanheest.shoppinglist

import javafx.scene.control._
import javafx.stage.{ Modality, Window }

class AddWindow(owner: Window) extends Dialog[String] { window =>
  private val addDialog = new MyAddDialog

  initModality(Modality.APPLICATION_MODAL)
  initOwner(owner)
  setDialogPane(addDialog)
  setResultConverter {
    case ButtonType.APPLY => addDialog.getText
    case ButtonType.CANCEL => null
  }
}
