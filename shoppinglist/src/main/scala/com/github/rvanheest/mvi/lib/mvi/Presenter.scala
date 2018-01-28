package com.github.rvanheest.mvi.lib.mvi

trait Presenter[V <: View] {
  def attachView(view: V): Unit

  def detachView(): Unit
}
