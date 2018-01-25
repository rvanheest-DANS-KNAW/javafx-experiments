package com.github.rvanheest.validation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{ Button, Label, TextField }
import javafx.scene.layout.{ HBox, VBox }
import javafx.stage.Stage

import org.controlsfx.validation.decoration.StyleClassValidationDecoration
import org.controlsfx.validation.{ ValidationSupport, Validator }

class Validation extends Application {
  def start(primaryStage: Stage): Unit = {
    val validation = new ValidationSupport {
      setErrorDecorationEnabled(false)
      setValidationDecorator(new StyleClassValidationDecoration("error", "warning"))
    }

    val lb1 = new Label("username")
    val tf1 = new TextField() {
      setPromptText("username")
      validation.registerValidator(this, Validator.createEmptyValidator("Username is required"))
    }
    val hbox1 = new HBox(3.0, lb1, tf1)

    val lb2 = new Label("password")
    val tf2 = new TextField() {
      setPromptText("password")
      validation.registerValidator(this, Validator.createPredicateValidator[String]((s: String) => s.length > 5, "Password length should be greater than 5"))
    }
    val hbox2 = new HBox(3.0, lb2, tf2)

    // TODO also add other form elements (radio buttons, etc.) to see how validation can be done there

    val btn = new Button("validate...") {
      setOnAction(_ => {
        validation.setErrorDecorationEnabled(true)
        validation.initInitialDecoration()
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

object Validation extends App {
  Application.launch(classOf[Validation], args: _*)
}
