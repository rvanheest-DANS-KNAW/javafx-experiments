package com.github.rvanheest.mvi.lib.mvi

import rx.lang.scala.Subscriber
import rx.lang.scala.subjects.BehaviorSubject


class DisposableViewStateObserver[T](viewStateBehaviorSubject: BehaviorSubject[T]) extends Subscriber[T] {
  override def onNext(t: T): Unit = viewStateBehaviorSubject.onNext(t)

  override def onError(e: Throwable): Unit = {
    throw new IllegalStateException("ViewState Observable must never reach error state - onError()", e)
  }

  override def onCompleted(): Unit = {
    // ViewState Observable never completes so ignore any complete event
  }
}
