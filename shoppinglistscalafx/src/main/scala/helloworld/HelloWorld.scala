package helloworld

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.StackPane

object HelloWorld extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "Hello World!"
    width = 300
    height = 250
    scene = new Scene {
      content = new StackPane {
        children = Seq(
          new Button {
            text = "Say 'Hello World'"
            onAction = { _ => println("Hello World!") }
          }
        )
      }
    }
  }

  // TODO onderzoek hoe dit werkt met RxJava
  // TODO onderzoek of dit ook modulair kan, dus maak de shoppinglist na
  // TODO onderzoek de Scene Builder, vraag na bij Alexander
}
