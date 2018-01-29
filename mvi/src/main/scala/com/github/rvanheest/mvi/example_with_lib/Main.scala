package com.github.rvanheest.mvi.example_with_lib

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.stage.Stage

// TODO note that this example doesn't take into account the unsubscribe times of the Presenters!
class Main extends Application {

  def start(primaryStage: Stage): Unit = {
    val root = new BmiPanel {
      setPadding(new Insets(10))
    }

    primaryStage.setScene(new Scene(root, 300, 250))
    primaryStage.setOnCloseRequest(_ => {
      if (!root.isUnsubscribed)
        root.unsubscribe()
    })
    primaryStage.setTitle("BMI calculator")
    primaryStage.show()
  }
}

object Main extends App {
  Application.launch(classOf[Main], args: _*)
}
