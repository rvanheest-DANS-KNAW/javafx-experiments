package com.github.rvanheest.mvi

import io.reactivex.Observable
import io.reactivex.disposables.{ CompositeDisposable, Disposable }
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.{ BehaviorSubject, PublishSubject }

import scala.collection.mutable.ListBuffer

class DisposableViewStateObserver[T](viewStateBehaviorSubject: BehaviorSubject[T]) extends DisposableObserver[T] {
  override def onNext(t: T): Unit = viewStateBehaviorSubject.onNext(t)

  override def onError(e: Throwable): Unit = {
    throw new IllegalStateException("ViewState Observable must never reach error state - onError()", e)
  }

  override def onComplete(): Unit = {
    // ViewState Observable never completes so ignore any complete event
  }
}

class DisposableIntentObserver[Intent](subject: PublishSubject[Intent]) extends DisposableObserver[Intent] {
  override def onNext(intent: Intent): Unit = {
    subject.onNext(intent)
  }

  override def onError(e: Throwable): Unit = {
    throw new IllegalStateException("View intents must not throw errors", e)
  }

  override def onComplete(): Unit = {
    subject.onComplete()
  }
}

trait MviView

abstract class BasePresenter[V <: MviView, VS] private (viewStateBehaviorSubject: BehaviorSubject[VS]) {

  def this(vs: VS) {
    this(BehaviorSubject.createDefault(vs))
  }

  def this() {
    this(BehaviorSubject.create[VS])
  }

  protected type ViewIntentBinder[Intent] = V => Observable[Intent]

  protected type ViewStateConsumer = (V, VS) => Unit

  private case class IntentRelayBinderPair[Intent](intentRelaySubject: PublishSubject[Intent], intentBinder: ViewIntentBinder[Intent])

  private var subscribeViewStateMethodCalled = false
  private val intentRelaysBinders = new ListBuffer[IntentRelayBinderPair[_]]
  private var intentDisposables: CompositeDisposable = _
  private var viewRelayConsumerDisposable: Disposable = _
  private var viewStateDisposable: Disposable = _
  private var viewAttachedFirstTime = true
  private var viewStateConsumer: ViewStateConsumer = _
  reset()

  protected def viewStateObservable: Observable[VS] = viewStateBehaviorSubject

  def attachView(view: V): Unit = {
    if (viewAttachedFirstTime)
      bindIntents()

    if (viewStateConsumer != null)
      subscribeViewStateConsumerActually(view)

    val intentsSize = intentRelaysBinders.size
    for (i <- 0 until intentsSize) {
      val intentRelayBinderPair = intentRelaysBinders(i)
      bindIntentActually(view, intentRelayBinderPair)
    }

    viewAttachedFirstTime = false
  }

  def detachView(): Unit = {
    detachView(true)
    if (viewRelayConsumerDisposable != null) {
      viewRelayConsumerDisposable.dispose()
      viewRelayConsumerDisposable = null
    }
    if (intentDisposables != null) {
      intentDisposables.dispose()
      intentDisposables = null
    }
  }

  def destroy(): Unit = {
    detachView(false)
    if (viewStateDisposable != null) { // Cancel the overall observable stream
      viewStateDisposable.dispose()
    }
    unbindIntents()
    reset()
  }

  @deprecated
  def detachView(retainInstance: Boolean): Unit = {}

  private def reset(): Unit = {
    viewAttachedFirstTime = true
    intentRelaysBinders.clear
    subscribeViewStateMethodCalled = false
  }

  protected def subscribeViewState(viewStateObservable: Observable[VS], consumer: ViewStateConsumer): Unit = {
    if (subscribeViewStateMethodCalled)
      throw new IllegalStateException("subscribeViewState() method is only allowed to be called once")
    subscribeViewStateMethodCalled = true

    if (viewStateObservable == null)
      throw new NullPointerException("ViewState Observable is null")

    if (consumer == null)
      throw new NullPointerException("ViewStateBinder is null")

    this.viewStateConsumer = consumer

    viewStateDisposable = viewStateObservable.subscribeWith(new DisposableViewStateObserver(viewStateBehaviorSubject))
  }

  private def subscribeViewStateConsumerActually(view: V): Unit = {
    if (view == null)
      throw new NullPointerException("View is null")

    if (viewStateConsumer == null)
      throw new NullPointerException(s"${classOf[ViewStateConsumer].getSimpleName} is null. " +
        "This is an internal bug. Please let me know!")

    viewRelayConsumerDisposable = viewStateBehaviorSubject.subscribe(vs => viewStateConsumer(view, vs))
  }

  protected def bindIntents(): Unit

  protected def unbindIntents(): Unit = {}

  def intent[Intent](binder: ViewIntentBinder[Intent]): Observable[Intent] = {
    val intentRelay = PublishSubject.create[Intent]
    intentRelaysBinders append IntentRelayBinderPair(intentRelay, binder)
    intentRelay
  }

  private def bindIntentActually[Intent](view: V, relayBinderPair: IntentRelayBinderPair[Intent]): Observable[Intent] = {
    if (view == null)
      throw new NullPointerException("View is null. This is an internal bug. Please let me know!")

    if (relayBinderPair == null)
      throw new NullPointerException("IntentRelayBinderPair is null. This is an internal bug. Please let me know!")

    val IntentRelayBinderPair(intentRelay, intentBinder) = relayBinderPair
    if (intentRelay == null)
      throw new NullPointerException("IntentRelay from binderPair is null. This is an internal bug. Please let me know!")
    if (intentBinder == null)
      throw new NullPointerException(classOf[ViewIntentBinder[Intent]].getSimpleName + " is null. This is an internal bug. Please let me know!")

    val intent = intentBinder(view)
    if (intent == null)
      throw new NullPointerException("Intent Observable returned from Binder " + intentBinder + " is null")

    if (intentDisposables == null)
      intentDisposables = new CompositeDisposable

    intentDisposables.add(intent.subscribeWith(new DisposableIntentObserver(intentRelay)))
    intentRelay
  }
}
