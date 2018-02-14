package com.github.rvanheest.shoppinglist.presenter

import com.github.rvanheest.shoppinglist.backend.ShoppingListAPI
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ MenuBarView, MenuBarViewState }
import org.junit.runner.RunWith
import org.junit.{ After, Before, Test }
import org.mockito.Matchers.{ eq => matches, _ }
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.runners.MockitoJUnitRunner
import org.testfx.framework.junit.ApplicationTest
import rx.lang.scala.Observable

@RunWith(classOf[MockitoJUnitRunner])
class MenuBarPresenterTest extends ApplicationTest {

  class TestInteractor extends ShoppingListInteractor(mock(classOf[ShoppingListAPI]))

  @Mock private var mockInteractor: TestInteractor = _
  @Mock private var mockView: MenuBarView = _
  private var presenter: MenuBarPresenter = _

  @Before
  def setUp(): Unit = {
    presenter = new MenuBarPresenter(mockInteractor)
  }

  @After
  def tearDown(): Unit = {
    presenter.unsubscribe()
    presenter = null
  }

  @Test
  def testAddIntent(): Unit = {
    when(mockView.addIntent()).thenReturn(Observable.just(()))
    when(mockView.addShoppingItemIntent()).thenReturn(Observable.empty)
    when(mockView.clearIntent()).thenReturn(Observable.empty)
    when(mockView.closeIntent()).thenReturn(Observable.empty)

    presenter.attachView(mockView)

    verify(mockView).render(matches(MenuBarViewState(true)))
  }

  @Test
  def testAddShoppingItemIntent(): Unit = {
    when(mockView.addIntent()).thenReturn(Observable.empty)
    when(mockView.addShoppingItemIntent()).thenReturn(Observable.just("milk"))
    when(mockView.clearIntent()).thenReturn(Observable.empty)
    when(mockView.closeIntent()).thenReturn(Observable.empty)
    when(mockInteractor.add(any())).thenReturn(Observable.just(()))

    presenter.attachView(mockView)

    verify(mockView).render(matches(MenuBarViewState(false)))
    verify(mockInteractor).add(matches("milk"))
    verifyNoMoreInteractions(mockInteractor)
  }

  @Test
  def testClearIntent(): Unit = {
    when(mockView.addIntent()).thenReturn(Observable.empty)
    when(mockView.addShoppingItemIntent()).thenReturn(Observable.empty)
    when(mockView.clearIntent()).thenReturn(Observable.just(()))
    when(mockView.closeIntent()).thenReturn(Observable.empty)
    when(mockInteractor.clear()).thenReturn(Observable.just(()))

    presenter.attachView(mockView)

    verify(mockView).addShoppingItemIntent()
    verify(mockInteractor).clear()
    verifyNoMoreInteractions(mockInteractor)
  }
}
