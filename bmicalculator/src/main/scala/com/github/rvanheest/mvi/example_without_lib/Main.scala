package com.github.rvanheest.mvi.example_without_lib

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.stage.Stage

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
