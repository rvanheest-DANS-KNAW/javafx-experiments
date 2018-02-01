package com.github.rvanheest.mvi.example_without_lib

object Injection {

  private lazy val bmiCalculator = new BmiCalculator

  private def newBmiInteractor(): BmiInteractor = new BmiInteractor(bmiCalculator)

  def newBmiPresenter(): BmiPresenter = new BmiPresenter(newBmiInteractor())
}
