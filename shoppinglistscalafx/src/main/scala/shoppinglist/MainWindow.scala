package shoppinglist

import io.reactivex.disposables.Disposable

import scalafx.application.JFXApp
import scalafx.scene.{ Node, Scene }

object MainWindow extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title.value = "Shopping list"
    width = 300
    height = 250
  }

  val app: Node with Disposable = new MyApp(() => new AddWindow(stage))

  stage.scene = new Scene {
    stylesheets = Seq(
      "style.css",
      "bootstrapfx.css"
    )
    content = app
  }
  stage.onCloseRequest = _ => app.dispose()
}
