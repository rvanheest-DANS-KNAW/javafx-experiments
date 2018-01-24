package shoppinglist

import scalafx.scene.control.{ Button, ButtonType, DialogPane, TextField }
import scalafx.scene.layout.VBox

class MyAddDialog extends DialogPane {

  buttonTypes = Seq(ButtonType.Apply, ButtonType.Cancel)

  private val textfield = new TextField {
    onAction = _ => lookupButton(ButtonType.Apply).asInstanceOf[Button].fire
  }
  content = new VBox(10.0, new MyHeader("Enter Item"), textfield)

  def text: String = textfield.text()

  // TODO not sure how to override createButton, to do styling on these buttons
}
