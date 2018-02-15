package com.github.rvanheest.shoppinglist.presenter

import com.github.rvanheest.shoppinglist.UITest
import com.github.rvanheest.shoppinglist.backend.ShoppingListAPI
import com.github.rvanheest.shoppinglist.interactor.ShoppingListInteractor
import com.github.rvanheest.shoppinglist.ui.{ ShoppingListState, ShoppingListView }
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.{ After, Before, Test }
import org.mockito.Matchers.{ eq => matches, _ }
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.runners.MockitoJUnitRunner
import org.testfx.framework.junit.ApplicationTest
import rx.lang.scala.Observable

@Category(Array(classOf[UITest]))
@RunWith(classOf[MockitoJUnitRunner])
class ShoppingListPresenterTest extends ApplicationTest {

  class TestInteractor extends ShoppingListInteractor(mock(classOf[ShoppingListAPI]))

  @Mock private var mockInteractor: TestInteractor = _
  @Mock private var mockView: ShoppingListView = _
  private var presenter: ShoppingListPresenter = _

  @Before
  def setUp(): Unit = {
    presenter = new ShoppingListPresenter(mockInteractor)
  }

  @After
  def tearDown(): Unit = {
    presenter.unsubscribe()
    presenter = null
  }

  @Test
  def testDoubleClickListItemIntent(): Unit = {
    when(mockView.doubleClickListItemIntent).thenReturn(Observable.just(3))
    when(mockInteractor.remove(any())).thenReturn(Observable.just(()))
    when(mockInteractor.getItems).thenReturn(Observable.empty)

    presenter.attachView(mockView)

    verify(mockInteractor).remove(matches(3))
  }

  @Test
  def testInteractorGetItems(): Unit = {
    val state = ShoppingListState.Result(Seq("milk", "sugar"))

    when(mockView.doubleClickListItemIntent).thenReturn(Observable.empty)
    when(mockInteractor.getItems).thenReturn(Observable.just(state))

    presenter.attachView(mockView)

    verify(mockView).render(matches(state))
  }
}
