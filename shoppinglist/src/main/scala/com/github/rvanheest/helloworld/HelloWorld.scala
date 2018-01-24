package com.github.rvanheest.helloworld

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class HelloWorld extends Application {
  def start(primaryStage: Stage): Unit = {
    val btn = new Button("Say 'Hello World'")
    btn.setOnAction((e: ActionEvent) => println("Hello World!"))

    val root = new StackPane
    root.getChildren.add(btn)

    primaryStage.setScene(new Scene(root, 300, 250))
    primaryStage.setTitle("Hello World!")
    primaryStage.show()
  }
}

object HelloWorld extends App {
  Application.launch(classOf[HelloWorld], args: _*)
}
