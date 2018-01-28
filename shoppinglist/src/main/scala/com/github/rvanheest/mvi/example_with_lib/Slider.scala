package com.github.rvanheest.mvi.example_with_lib

import javafx.scene.control.{ Label, Slider => JFXSlider }
import javafx.scene.layout.HBox

import com.github.rvanheest.mvi.lib.mvi.{ BasePresenter, View }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Observable
import rx.observables.JavaFxObservable

/*
 * Define Model
 * Define View interface with the intents and the render function
 * Define Presenter extension from BasePresenter[View, Model], maybe with an initial Model and/or state reducer function
 *    Presenter also holds reference to backend (via injection/constructor)
 * Define UI component, extending the View and MviViewElement interfaces
 */

trait SliderView extends View {
  def sliderIntent: Observable[Int]

  def render(model: Int): Unit
}

class SliderPresenter extends BasePresenter[SliderView, Int] {

  override protected def bindIntents(): Unit = subscribeViewState(intent(_.sliderIntent), _ render _)
}

class Slider(labelText: String, unit: String, min: Int, max: Int) extends HBox(10.0) with SliderView {

  private val label: Label = new Label()
  private val slider: JFXSlider = new JFXSlider(min, max, (min + max) / 2)
  getChildren.addAll(label, slider)

  val presenter = new SliderPresenter
  presenter.attachView(this)

  override def render(model: Int): Unit = {
    label.setText(s"$labelText: $model$unit")
    slider.setValue(model)
  }

  override def sliderIntent: Observable[Int] = {
    JavaFxObservable.valuesOf(slider.valueProperty()).asScala
      .map(_.intValue())
      .distinctUntilChanged
  }
}
