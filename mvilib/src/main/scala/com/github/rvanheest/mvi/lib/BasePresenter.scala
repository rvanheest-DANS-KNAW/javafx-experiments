package com.github.rvanheest.mvi.lib

import rx.lang.scala.JavaConverters._
import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.subscriptions.CompositeSubscription
import rx.lang.scala.{ Observable, Subscription }
import rx.subjects.PublishSubject

import scala.collection.mutable.ListBuffer

abstract class BasePresenter[V <: View, ViewState] private(viewStateBehaviorSubject: BehaviorSubject[ViewState]) extends Presenter[V] {

  protected type ViewIntentBinder[Intent] = V => Observable[Intent]

  protected type ViewStateConsumer = (V, ViewState) => Unit

  private case class IntentRelayBinderPair[Intent](intentRelaySubject: PublishSubject[Intent], intentBinder: ViewIntentBinder[Intent])

  def this(viewState: ViewState) = this(BehaviorSubject(viewState))

  def this() = this(BehaviorSubject[ViewState])

  private var subscribeViewStateMethodCalled = false
  private val intentRelaysBinders = new ListBuffer[IntentRelayBinderPair[_]]
  private var intentSubscriptions: CompositeSubscription = _
  private var viewRelayConsumerSubscription: Subscription = _
  private var viewStateSubscription: Subscription = _
  private var viewAttachedFirstTime = true
  private var viewStateConsumer: ViewStateConsumer = _
  reset()

  protected def viewStateObservable: Observable[ViewState] = viewStateBehaviorSubject

  override def attachView(view: V): Unit = {
    if (viewAttachedFirstTime)
      bindIntents()

    if (viewStateConsumer != null)
      subscribeViewStateConsumerActually(view)

    for (IntentRelayBinderPair(subject, binder) <- intentRelaysBinders)
      bindIntentActually(view, subject, binder)

    viewAttachedFirstTime = false
  }

  override def isUnsubscribed: Boolean = {
    List(viewRelayConsumerSubscription, intentSubscriptions, viewStateSubscription).forall(_.isUnsubscribed) && super.isUnsubscribed
  }

  override def unsubscribe(): Unit = {
    if (viewRelayConsumerSubscription != null) {
      viewRelayConsumerSubscription.unsubscribe()
      viewRelayConsumerSubscription = null
    }

    if (intentSubscriptions != null) {
      intentSubscriptions.unsubscribe()
      intentSubscriptions = null
    }

    if (viewStateSubscription != null) // Cancel the overall observable stream
      viewStateSubscription.unsubscribe()

    unbindIntents()
    reset()
    super.unsubscribe()
  }

  private def reset(): Unit = {
    viewAttachedFirstTime = true
    intentRelaysBinders.clear
    subscribeViewStateMethodCalled = false
  }

  protected def subscribeViewState(viewStateObservable: Observable[ViewState], consumer: ViewStateConsumer): Unit = {
    if (subscribeViewStateMethodCalled)
      throw new IllegalStateException("subscribeViewState() method is only allowed to be called once")
    subscribeViewStateMethodCalled = true

    if (viewStateObservable == null)
      throw new NullPointerException("ViewState Observable is null")

    if (consumer == null)
      throw new NullPointerException("ViewStateBinder is null")

    viewStateConsumer = consumer

    viewStateSubscription = viewStateObservable.subscribe(new DisposableViewStateObserver(viewStateBehaviorSubject))
  }

  private def subscribeViewStateConsumerActually(view: V): Unit = {
    if (view == null)
      throw new NullPointerException("View is null")

    if (viewStateConsumer == null)
      throw new NullPointerException(s"${classOf[ViewStateConsumer].getSimpleName} is null. " +
        "This is an internal bug. Please let me know!")

    viewRelayConsumerSubscription = viewStateBehaviorSubject.subscribe(vs => viewStateConsumer(view, vs))
  }

  protected def bindIntents(): Unit

  protected def unbindIntents(): Unit = {}

  def intent[Intent](binder: ViewIntentBinder[Intent]): Observable[Intent] = {
    val intentRelay = PublishSubject.create[Intent]
    intentRelaysBinders append IntentRelayBinderPair(intentRelay, binder)
    intentRelay.asObservable().asScala
  }

  private def bindIntentActually[Intent](view: V, intentRelay: PublishSubject[Intent], intentBinder: ViewIntentBinder[Intent]): Observable[Intent] = {
    if (view == null)
      throw new NullPointerException("View is null. This is an internal bug. Please let me know!")

    if (intentRelay == null)
      throw new NullPointerException("IntentRelay from binderPair is null. This is an internal bug. Please let me know!")
    if (intentBinder == null)
      throw new NullPointerException(classOf[ViewIntentBinder[Intent]].getSimpleName + " is null. This is an internal bug. Please let me know!")

    val intent = intentBinder(view)
    if (intent == null)
      throw new NullPointerException("Intent Observable returned from Binder " + intentBinder + " is null")

    if (intentSubscriptions == null)
      intentSubscriptions = CompositeSubscription()

    intentSubscriptions += intent.subscribe(new DisposableIntentObserver(intentRelay))
    intentRelay.asObservable().asScala
  }
}
