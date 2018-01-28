package com.github.rvanheest.mvi.example_without_lib

import javafx.scene.control.Label
import javafx.scene.layout.VBox

import rx.lang.scala.JavaConverters._
import rx.lang.scala.subscriptions.CompositeSubscription
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
class BmiInteractor(bmiCalculator: BmiCalculator) {

  def calculateBmi(weight: Int, height: Int): Observable[BmiViewState] = {
    BmiLoading +: bmiCalculator.calcBmi(weight, height)
      .map(BmiResult)
      .onErrorReturn(BmiError)
  }
}

// declares the intent Observables and the render function
trait BmiView {
  def weightIntent: Observable[Int]

  def heightIntent: Observable[Int]

  def render(viewState: BmiViewState): Unit
}

class BmiPresenter(bmiInteractor: BmiInteractor) extends Subscription {

  private val subscriptions = CompositeSubscription()

  def attachView(view: BmiView): Unit = {
    subscriptions += view.weightIntent.combineLatest(view.heightIntent)
      .map { case (w, h) => bmiInteractor.calculateBmi(w, h) }
      .switch
      .observeOn(JavaFxScheduler.platform().asScala)
      .subscribe(state => view.render(state))
  }

  override def isUnsubscribed: Boolean = subscriptions.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    subscriptions.unsubscribe()
    super.unsubscribe()
  }
}

class BmiPanel extends VBox(10.0) with BmiView with Subscription {

  private val weight = new Slider("weight", "kg", 40, 150)
  private val height = new Slider("height", "cm", 0, 220)
  private val label = new Label()
  private val presenter = Injection.newBmiPresenter()
  private val subscription = CompositeSubscription() += weight += height += presenter
  getChildren.addAll(weight, height, label)
  presenter.attachView(this)

  override def weightIntent: Observable[Int] = weight.sliderIntent

  override def heightIntent: Observable[Int] = height.sliderIntent

  override def render(viewState: BmiViewState): Unit = viewState match {
    case BmiLoading => label.setText("calculating...")
    case BmiResult(bmi) => label.setText(s"BMI: $bmi")
    case BmiError(error) => label.setText(s"An error occurred: ${ error.getMessage }")
  }

  override def isUnsubscribed: Boolean = subscription.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    subscription.unsubscribe()
    super.unsubscribe()
  }
}
