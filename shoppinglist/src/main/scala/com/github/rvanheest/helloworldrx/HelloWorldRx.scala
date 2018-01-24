package com.github.rvanheest.helloworldrx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import io.reactivex.rxjavafx.observables.JavaFxObservable

class HelloWorldRx extends Application {
  def start(primaryStage: Stage): Unit = {
    val btn = new Button("Say 'Hello World'")

    JavaFxObservable.actionEventsOf(btn)
      .subscribe(_ => println("Hello World!"))

    val root = new StackPane
    root.getChildren.add(btn)

    primaryStage.setScene(new Scene(root, 300, 250))
    primaryStage.setTitle("Hello World!")
    primaryStage.show()
  }
}

object HelloWorldRx extends App {
  Application.launch(classOf[HelloWorldRx], args: _*)
}
