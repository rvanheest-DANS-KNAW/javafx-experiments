package com.github.rvanheest.mvi.lib

import rx.lang.scala.Subscription

trait Presenter[V <: View] extends Subscription {
  def attachView(view: V): Unit
}
