package com.github.rvanheest.mvi.example_with_lib

object Injection {

  // backend services as singletons, lazily instantiated
  private lazy val bmiCalculator = new BmiCalculator

  // interactors as private functions, calling on the backend services as needed
  private def newBmiInteractor(): BmiInteractor = new BmiInteractor(bmiCalculator)

  // presenters as public functions, calling on the interactors as needed
  def newBmiPresenter(): BmiPresenter = new BmiPresenter(newBmiInteractor())
}
