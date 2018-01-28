package com.github.rvanheest.mvi.example_without_lib

import javafx.scene.control.{ Label, Slider => JFXSlider }
import javafx.scene.layout.HBox

import rx.lang.scala.JavaConverters._
import rx.lang.scala.{ Observable, Subscription }
import rx.observables.JavaFxObservable

class Slider(labelText: String, unit: String, min: Int, max: Int) extends HBox(10.0) with Subscription {

  private val label: Label = new Label()
  private val slider: JFXSlider = new JFXSlider(min, max, (min + max) / 2)
  getChildren.addAll(label, slider)

  private val subscription = sliderIntent.subscribe(v => this.render(v))

  def render(model: Int): Unit = {
    label.setText(s"$labelText: $model$unit")
    slider.setValue(model)
  }

  def sliderIntent: Observable[Int] = {
    JavaFxObservable.valuesOf(slider.valueProperty()).asScala
      .map(_.intValue())
      .distinctUntilChanged
  }

  override def isUnsubscribed: Boolean = subscription.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    subscription.unsubscribe()
    super.unsubscribe()
  }
}
