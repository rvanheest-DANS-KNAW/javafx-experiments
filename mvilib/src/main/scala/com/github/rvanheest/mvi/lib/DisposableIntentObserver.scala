package com.github.rvanheest.mvi.lib

import rx.lang.scala.Subscriber
import rx.subjects.PublishSubject

class DisposableIntentObserver[Intent](subject: PublishSubject[Intent]) extends Subscriber[Intent] {
  override def onNext(intent: Intent): Unit = {
    subject.onNext(intent)
  }

  override def onError(e: Throwable): Unit = {
    throw new IllegalStateException("View intents must not throw errors", e)
  }

  override def onCompleted(): Unit = {
    subject.onCompleted()
  }
}
