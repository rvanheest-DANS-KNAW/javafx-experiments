package shoppinglist

import scalafx.scene.control.{ ButtonType, Dialog }
import scalafx.stage.{ Modality, Window }

class AddWindow(owner: Window) extends Dialog[String] {
  private val addDialog = new MyAddDialog

  initModality(Modality.ApplicationModal)
  initOwner(owner)

  dialogPane = addDialog
  resultConverter = {
    case ButtonType.Apply => addDialog.text
    case ButtonType.Cancel => null
  }
}
