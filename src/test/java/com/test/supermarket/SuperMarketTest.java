package com.test.supermarket;

import com.test.supermarket.basket.ShoppingBasket;
import com.test.supermarket.model.Product;
import com.test.supermarket.model.ShoppingDiscount;
import com.test.supermarket.model.ShoppingListItem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;


public class SuperMarketTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void init(){
        Set<Product> products = new HashSet<Product>();
        Product product = new Product();
        product.setName("Apple");
        product.setPrice(new BigDecimal("0.35"));
        product.setProductCode("Apple");
        products.add(product);

        product = new Product();
        product.setName("Banana");
        product.setPrice(new BigDecimal("0.20"));
        product.setProductCode("Banana");
        products.add(product);

        product = new Product();
        product.setName("Melon");
        product.setPrice(new BigDecimal("0.50"));
        product.setProductCode("Melon");
        products.add(product);

        product = new Product();
        product.setName("Lime");
        product.setPrice(new BigDecimal("0.15"));
        product.setProductCode("Lime");
        products.add(product);

        ShoppingTill.setProductOffering(products);
        addDiscounts();

    }

    @Test
    public void whenItemsAreAddedToBasketReturnCorrectTotalAmount (){
        Double expectedTotal = 0.35 + 0.35 + 0.20;
        BigDecimal total = new BigDecimal(expectedTotal).
                setScale( 2, RoundingMode.HALF_UP);;
        String[] shoppingList = {"Apple", "Apple", "Banana"};
        BigDecimal billTotal = ShoppingTill.calculateBill(shoppingList);
        assertEquals(total,billTotal);
    }

    @Test
    public void whenDiscountedItemsAreAddedToBasketCorrectTotalsAmountCalculated(){
        Double expectedTotal =
                0.35 + 0.35 + 0.20 + 0.15 + 0.15 + 0.15 + 0.50;
        BigDecimal total = new BigDecimal(expectedTotal).
                setScale( 2, RoundingMode.HALF_UP);;
        String[] shoppingList =
                {"Apple", "Apple", "Banana","Lime","Lime","Lime","Lime","Melon","Melon"};
        BigDecimal billTotal = ShoppingTill.calculateBill(shoppingList);
        assertEquals(total, billTotal);
    }

    @Test
    public void testCatalogue(){
        Product product = ShoppingTill.getProductByCode("Apple");
        Assert.assertEquals(product.getProductCode(),"Apple");
        Assert.assertEquals(product.getPrice(),new BigDecimal("0.35"));
    }

    @Test
    public void whenShoppingItemIsAddedToBasketItShouldBeAvaialble(){
        ShoppingBasket basket = new ShoppingBasket( );
        ShoppingListItem listItem = new ShoppingListItem("Apple");
        basket.addItem(listItem);

        Collection<ShoppingListItem> items = basket.getShoppingListItems();
        assertTrue(items.contains(listItem));
    }


    @Test()
    public void attemptToAddInvalidItemToBasketShouldThrowException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot find product with product code : ");
        ShoppingBasket basket = new ShoppingBasket( );
        ShoppingListItem listItem = new ShoppingListItem("bananas");
        basket.addItem(listItem);
    }

    @Test
    public void whenItemsAreAddedToBaskeItemTallyShould(){
        ShoppingBasket basket = new ShoppingBasket( );
        ShoppingListItem listItem = new ShoppingListItem("Apple");
        basket.addItem(listItem);
        basket.addItem(listItem);
        Collection<ShoppingListItem> items = basket.getShoppingListItems();
        assertTrue(items.contains(listItem));
        Optional<ShoppingListItem> optionalItem =
                items.stream().filter(lineItem -> lineItem.getProductCode().equals("Apple")).findFirst();
        Assert.assertEquals(2,optionalItem.get().getQuantity(),0.00001);
    }

    public static void addDiscounts(){
        Set<ShoppingDiscount> discounts = new HashSet<ShoppingDiscount>();
        ShoppingDiscount discount = new ShoppingDiscount();
        discount.setDiscountAmount(0.5);
        discount.setProductCode("Melon");
        discount.setTriggerQuantity(1.0);
        discount.setDiscountDescription("buy one get one free");
        discounts.add(discount);

        discount = new ShoppingDiscount();
        discount.setDiscountAmount(0.333);
        discount.setProductCode("Lime");
        discount.setTriggerQuantity(3.0);
        discount.setDiscountDescription("3 tomatoes for 2");
        discounts.add(discount);

        ShoppingTill.setDiscounts(discounts);
    }
}
