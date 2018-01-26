package com.github.rvanheest.mvi

import javafx.scene.Node
import javafx.scene.control.{ Label, Slider => JFXSlider }
import javafx.scene.layout.HBox

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.observables.JavaFxObservable

import scala.language.implicitConversions

class Slider(initModel: SliderModel) extends Disposable {

  private val ui = new SliderUI
  private val presenter = new SliderPresenter(initModel)

  presenter.attachView(ui)

  def value: Observable[SliderModel] = presenter.viewStateObservable

  private var disposed = false
  override def isDisposed: Boolean = disposed

  override def dispose(): Unit = {
    presenter.detachView()
    disposed = true
  }
}
object Slider {
  implicit def asNode(slider: Slider): Node = slider.ui
}

// presenter
class SliderPresenter(initModel: SliderModel) extends BasePresenter[SliderUI, SliderModel] {

//  private val ui = new SliderUI

//  private val viewStateBehaviorSubject = BehaviorSubject.createDefault(initModel)
//  def viewStateObservable: Observable[SliderModel] = viewStateBehaviorSubject

//  private val stateObs = ui.sliderIntent
//    .startWith(initModel.value)
//    .scan(initModel, (oldModel: SliderModel, value: Int) => oldModel.copy(value = value))

//  private val viewStateDisposable = stateObs.subscribeWith(new DisposableViewStateObserver(viewStateBehaviorSubject))

//  private val viewRenderDisposable = viewStateBehaviorSubject.subscribe(model => ui.render(model))

//  private val compositeDisposable = new CompositeDisposable(viewStateDisposable, viewRenderDisposable)

//  override def isDisposed: Boolean = compositeDisposable.isDisposed

//  override def dispose(): Unit = compositeDisposable.dispose()

  override protected def bindIntents(): Unit = {
    val sliderModel = intent(_.sliderIntent)
      .startWith(initModel.value)
      .scan(initModel, (oldModel: SliderModel, value: Int) => oldModel.copy(value = value))

    subscribeViewState(sliderModel, _ render _)
  }

  override def viewStateObservable: Observable[SliderModel] = super.viewStateObservable
}

case class SliderModel(value: Int, label: String, unit: String, min: Int, max: Int)

class SliderUI extends HBox(10.0) with MviView {

  private val label: Label = new Label()
  private val slider: JFXSlider = new JFXSlider()
  getChildren.addAll(label, slider)

  def render(model: SliderModel): Unit = {
    label.setText(s"${model.label}: ${model.value}${model.unit}")
    slider.setValue(model.value)
    slider.setMin(model.min)
    slider.setMax(model.max)
  }

  def sliderIntent: Observable[Int] = {
    JavaFxObservable.valuesOf(slider.valueProperty())
      .map[Int](_.intValue())
      .distinctUntilChanged()
  }
}


