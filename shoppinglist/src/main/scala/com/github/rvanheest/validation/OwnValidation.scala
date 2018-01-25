package com.github.rvanheest.validation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{ Button, Label, TextField }
import javafx.scene.layout.{ HBox, VBox }
import javafx.stage.Stage

class OwnValidation extends Application {
  def start(primaryStage: Stage): Unit = {
    val lb1 = new Label("username")
    val tf1 = new TextField() {
      setPromptText("username")
    }
    val hbox1 = new HBox(3.0, lb1, tf1)

    val lb2 = new Label("password")
    val tf2 = new TextField() {
      setPromptText("password")
    }
    val hbox2 = new HBox(3.0, lb2, tf2)

    val btn = new Button("validate...") {
      setOnAction(_ => {
        tf1.getStyleClass.remove("error")
        tf2.getStyleClass.remove("error")

        if (tf1.getText.isEmpty) tf1.getStyleClass.add("error")
        if (tf2.getText.length <= 5) tf2.getStyleClass.add("error")
      })
    }

    val root = new VBox(3.0, hbox1, hbox2, btn)

    primaryStage.setScene(new Scene(root) {
      getStylesheets.addAll("style.css")
    })
    primaryStage.setTitle("Hello World!")
    primaryStage.show()
  }
}

object OwnValidation extends App {
  Application.launch(classOf[OwnValidation], args: _*)
}
