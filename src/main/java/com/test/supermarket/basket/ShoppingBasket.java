package com.test.supermarket.basket;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import java.util.Optional;
import java.util.Set;

import com.test.supermarket.ShoppingTill;
import com.test.supermarket.model.Product;
import com.test.supermarket.model.ShoppingListItem;
import org.apache.log4j.Logger;


/**
 * @author kay
 *encapsulates a shopping basket
 */
public class ShoppingBasket {
    final static Logger LOGGER = Logger.getLogger(ShoppingBasket.class);

    private final Set<ShoppingListItem> listItem = new HashSet<ShoppingListItem>();

    public ShoppingBasket() {

    }

    /**
     * adds one or more items of a specific product to a shopping basket
     * @param item shopping list item
     */
    public void addItem(ShoppingListItem item) {
        if(!listItem.add(item)){
            listItem.stream().filter(lItem -> lItem.getProductCode().equals(item.getProductCode())).
                    findAny().ifPresent(lItem -> lItem.increaseQuantity(item.getQuantity()));
        }
    }

    public static BigDecimal getProductPrice(String productCode) {
        return ShoppingTill.getProductPrice(productCode);
    }

    public Collection<ShoppingListItem> getShoppingListItems() {
        return this.listItem;
    }

}
