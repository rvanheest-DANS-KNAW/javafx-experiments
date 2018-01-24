package com.github.rvanheest.shoppinglist

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

import scala.language.postfixOps

class MainWindow extends Application {

  def start(primaryStage: Stage): Unit = {
    val app = new MyApp(() => new AddWindow(primaryStage))

    primaryStage.setOnCloseRequest(_ => app.dispose())
    primaryStage.setScene(new Scene(app, 300, 250) {
      getStylesheets.addAll("style.css", "bootstrapfx.css")
    })
    primaryStage.setTitle("Shopping list")
    primaryStage.show()
  }
}

object MainWindow extends App {
  Application.launch(classOf[MainWindow], args: _*)
}
