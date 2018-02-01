package com.github.rvanheest.mvi.example_with_lib

import javafx.scene.control.Label
import javafx.scene.layout.VBox

import com.github.rvanheest.mvi.lib.{ BasePresenter, View }
import rx.lang.scala.JavaConverters._
import rx.lang.scala.subscriptions.CompositeSubscription
import rx.lang.scala.{ Observable, Subscription }
import rx.schedulers.JavaFxScheduler

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/*
 * Define BackendService, maybe with a Model
 * Define the ViewModel
 * Define the interaction between View and the BackendService(s) using an Interactor, which translates Model to ViewModel
 * Define View interface with the intents (effects triggered by user) and the render function (translating ViewModel to onscreen effects)
 * Define Presenter extension from BasePresenter[View, ViewModel], maybe with an initial ViewModel and/or state reducer function
 *    implements 'bindIntents' by binding the View's intents to the BackendService/Interactor and subscribing to the View's 'render' function
 * Define UI component, extending the View interface
 *    also binds the view
 */

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

// view model
sealed abstract class BmiViewState
case object BmiLoading extends BmiViewState
case class BmiResult(result: Int) extends BmiViewState
case class BmiError(error: Throwable) extends BmiViewState

// interactor
class BmiInteractor(bmiCalculator: BmiCalculator) {

  def calculateBmi(weight: Int, height: Int): Observable[BmiViewState] = {
    BmiLoading +: bmiCalculator.calcBmi(weight, height)
      .map(BmiResult)
      .onErrorReturn(BmiError)
  }
}

// interface for the view
trait BmiView extends View {
  def weightIntent: Observable[Int]

  def heightIntent: Observable[Int]

  def render(viewState: BmiViewState): Unit
}

// presenter
class BmiPresenter(bmiInteractor: BmiInteractor) extends BasePresenter[BmiView, BmiViewState] {

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
class BmiPanel extends VBox(10.0) with BmiView with Subscription {

  private val weight = new Slider("weight", "kg", 40, 150)
  private val height = new Slider("height", "cm", 0, 220)
  private val label = new Label()
  private val presenter = createPresenter()
  private val subscription = CompositeSubscription() += weight += height += presenter
  getChildren.addAll(weight, height, label)
  presenter.attachView(this)

  override def weightIntent: Observable[Int] = weight.sliderIntent

  override def heightIntent: Observable[Int] = height.sliderIntent

  override def render(viewState: BmiViewState): Unit = viewState match {
    case BmiLoading => label.setText("calculating...")
    case BmiResult(bmi) => label.setText(s"BMI: $bmi")
    case BmiError(error) => label.setText(s"An error occurred: ${error.getMessage}")
  }

  protected def createPresenter(): BmiPresenter = Injection.newBmiPresenter()

  override def isUnsubscribed: Boolean = subscription.isUnsubscribed && super.isUnsubscribed

  override def unsubscribe(): Unit = {
    subscription.unsubscribe()
    super.unsubscribe()
  }
}
