package shoppinglist

import scalafx.scene.control.Label
import scalafx.scene.layout.HBox

class MyHeader(text: String) extends HBox {
  children = new Label(text) {
    styleClass = Seq("panel-title") // TODO or should I do an addAll here?
  }
  styleClass = Seq("panel-heading", "center")
}
