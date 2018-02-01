package com.github.rvanheest.shoppinglist

import java.util.UUID

package object backend {

  case class ShoppingListItem(uuid: UUID = UUID.randomUUID(), text: String)
  type ShoppingList = Seq[ShoppingListItem]
}
