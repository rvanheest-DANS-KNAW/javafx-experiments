package com.github.rvanheest.validation

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.layout.{ HBox, VBox }
import javafx.scene.{ Node, Scene }
import javafx.stage.Stage

import org.controlsfx.tools.ValueExtractor
import org.controlsfx.validation.decoration.StyleClassValidationDecoration
import org.controlsfx.validation.{ Severity, ValidationResult, ValidationSupport, Validator }

class Validation extends Application {

  def tf1: ValidationSupport => Node = validation => {
    val lb1 = new Label("username")
    val tf1 = new TextField() {
      setPromptText("username")
      validation.registerValidator(this, Validator.createEmptyValidator("Username is required"))
    }
    new HBox(3.0, lb1, tf1)
  }
  
  def tf2: ValidationSupport => Node = validation => {
    val lb2 = new Label("password")
    val tf2 = new TextField() {
      setPromptText("password")
      validation.registerValidator(this, Validator.createPredicateValidator[String]((s: String) => s.length > 5, "Password length should be greater than 5"))
    }
    new HBox(3.0, lb2, tf2)
  }

  def comboBox: ValidationSupport => Node = validation => {
    new ComboBox[String] {
      getItems.addAll("", "Item A", "Item B", "Item C")
      validation.registerValidator(this, Validator.createEmptyValidator("ComboBox Selection required"))
    }
  }

  def choiceBox: ValidationSupport => Node = validation => {
    new ChoiceBox[String] {
      getItems.addAll("", "Item 1", "Item 2", "Item 3")
      validation.registerValidator(this, Validator.createEmptyValidator("ChoiceBox Selection required"))
    }
  }

  def radioButtons: ValidationSupport => Node = _ => {
    val tg = new ToggleGroup
    
    val rb1 = new RadioButton("Male") {
      setToggleGroup(tg)
    }

    val rb2 = new RadioButton("Female") {
      setToggleGroup(tg)
    }

    new VBox(5.0, rb1, rb2)
  }

  def btn: ValidationSupport => Node = _ => {
    new Button("validate...") {
//      setOnAction(_ => {
//        validation.setErrorDecorationEnabled(true)
//        validation.initInitialDecoration()
//      })
    }
  }

  def start(primaryStage: Stage): Unit = {
    val validation = new ValidationSupport {
//      setErrorDecorationEnabled(false)
//      setValidationDecorator(new StyleClassValidationDecoration("error", "warning"))
    }

    // TODO also add other form elements (radio buttons, etc.) to see how validation can be done there
    // radio buttons are not supported for validation (open issue since 2014, https://bitbucket.org/controlsfx/controlsfx/issues/396/cannot-validate-radio-buttons-with)
    val nodes = List(tf1, tf2, comboBox, choiceBox, radioButtons, btn).map(_ (validation))
    val root = new VBox(5.0, nodes: _*) {
      setPadding(new Insets(10.0))
    }

    primaryStage.setScene(new Scene(root, 300, 250) {
      getStylesheets.addAll("style.css")
    })
    primaryStage.setTitle("Hello World!")
    primaryStage.show()
  }
}

object Validation extends App {
  Application.launch(classOf[Validation], args: _*)
}
