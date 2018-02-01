package com.github.rvanheest.shoppinglist

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

import com.github.rvanheest.shoppinglist.ui.MyApp

class MainWindow extends Application {

  def start(primaryStage: Stage): Unit = {
    val app = new MyApp(primaryStage)

    primaryStage.setOnCloseRequest(_ => {
      if (!app.isUnsubscribed)
        app.unsubscribe()
    })
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
