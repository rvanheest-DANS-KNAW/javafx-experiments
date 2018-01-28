package com.github.rvanheest.mvi.example_with_lib

import javafx.scene.control.Label
import javafx.scene.layout.VBox

import com.github.rvanheest.mvi.lib.mvi.{ BasePresenter, View }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.{ Observable, Subscription }
import rx.schedulers.JavaFxScheduler

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

// backend service
class BmiCalculator {

  def calcBmi(weight: Int, height: Int): Observable[Int] = {
    if (height == 0)
      Observable.error(new IllegalArgumentException("height == 0"))
    else
      Observable.defer(Observable.just {
        val heightMeters = height * 0.01
        math.round(weight / (heightMeters * heightMeters)).toInt
      })
  }.delay(1 second)
}

// UI's model
sealed abstract class BmiViewState
case object BmiLoading extends BmiViewState
case class BmiResult(result: Int) extends BmiViewState
case class BmiError(error: Throwable) extends BmiViewState

// interaction between backend and frontend
// translates backend output to the state of the UI
class BmiInteractor(bmiCalculator: BmiCalculator = new BmiCalculator) {

  def calculateBmi(weight: Int, height: Int): Observable[BmiViewState] = {
    BmiLoading +: bmiCalculator.calcBmi(weight, height)
      .map(BmiResult)
      .onErrorReturn(BmiError)
  }
}

// interface for the view
// defines the intents and the function for rendering the UI's state
trait BmiView extends View {
  def weightIntent: Observable[Int]

  def heightIntent: Observable[Int]

  def render(viewState: BmiViewState): Unit
}

// presenter to the UI
// sends intents to the interactor/backend, receives the UI's state(s) and calls the rendering function with those
class BmiPresenter(bmiInteractor: BmiInteractor = new BmiInteractor) extends BasePresenter[BmiView, BmiViewState] with Subscription {

  override protected def bindIntents(): Unit = {
    val weight = intent(_.weightIntent)
    val height = intent(_.heightIntent)

    val bmi = weight.combineLatest(height)
      .map { case (w, h) => bmiInteractor.calculateBmi(w, h) }
      .switch
      .observeOn(JavaFxScheduler.platform().asScala)

    subscribeViewState(bmi, _ render _)
  }
}

// the UI element
class BmiPanel extends VBox(10.0) with BmiView {

  private val weight = new Slider("weight", "kg", 40, 150)
  private val height = new Slider("height", "cm", 0, 220)
  private val label = new Label()
  getChildren.addAll(weight, height, label)

  val presenter = new BmiPresenter()
  presenter.attachView(this)

  override def weightIntent: Observable[Int] = weight.sliderIntent

  override def heightIntent: Observable[Int] = height.sliderIntent

  override def render(viewState: BmiViewState): Unit = viewState match {
    case BmiLoading => label.setText("calculating...")
    case BmiResult(bmi) => label.setText(s"BMI: $bmi")
    case BmiError(error) => label.setText(s"An error occurred: ${error.getMessage}")
  }
}
