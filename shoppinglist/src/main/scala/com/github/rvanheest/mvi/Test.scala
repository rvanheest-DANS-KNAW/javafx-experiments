package com.github.rvanheest.mvi

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage

import io.reactivex.Observable

class Test extends Application {
  def calcBmi(weight: Int, height: Int): Int = {
    val heightMeters = height * 0.01
    math.round(weight / (heightMeters * heightMeters)).toInt
  }

  def start(primaryStage: Stage): Unit = {
    val weight = new Slider(SliderModel(40, "weight", "kg", 40, 150))
    val height = new Slider(SliderModel(140, "height", "cm", 140, 220))
    val label = new Label("<bmi>")

    val bmi = Observable.combineLatest[SliderModel, SliderModel, Int](weight.value, height.value, (w: SliderModel, h: SliderModel) => calcBmi(w.value, h.value))
    bmi.map[String](x => s"BMI: $x").subscribe(label.setText(_))

    val root = new VBox(10.0, weight, height, label)

    primaryStage.setScene(new Scene(root, 300, 250))
    primaryStage.setOnCloseRequest(_ => {
      if (!weight.isDisposed)
        weight.dispose()
      if (!height.isDisposed)
        height.dispose()
    })
    primaryStage.setTitle("Hello World!")
    primaryStage.show()
  }
}

object Test extends App {
  Application.launch(classOf[Test], args: _*)
}
