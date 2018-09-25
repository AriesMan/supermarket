package com.test.supermarket;

import com.test.supermarket.basket.ShoppingBasket;
import com.test.supermarket.model.Product;
import com.test.supermarket.model.ShoppingDiscount;
import com.test.supermarket.model.ShoppingListItem;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class ShoppingTill {
    final static Logger LOGGER = Logger.getLogger(ShoppingTill.class);

    private static final Set<ShoppingDiscount> discounts = new HashSet<>();

    private static final Set<Product> PRODUCT_SET = new HashSet<>();

    private static final int myNumDecimals = 2;

    /**
     * get product instance using the code
     * @param productCode
     * @return gets product instance
     */
    public static Product getProductByCode(String productCode) {
        Product searchResult = PRODUCT_SET.stream()
                .filter(product -> product.getProductCode().equals(productCode))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        String.format("Cannot find product with product code : [%s] in product offerings",
                        productCode)));
        return searchResult;
    }

    /**
     * get price of product
     * @param productCode
     * @return price of related product
     */
    public static BigDecimal getProductPrice(String productCode){
        BigDecimal productPrice = BigDecimal.ZERO;
        Product searchResult = getProductByCode(productCode);
        productPrice = searchResult.getPrice();
        return productPrice;
    }


    /**
     * adds any discounts entries to the invoice if any apply to the items purchased
     * @param listItem item purchased
     * @return total discount amount for item purchased
     */
    public static BigDecimal calculateDiscountTotal(ShoppingListItem listItem){
        BigDecimal discountAmount = BigDecimal.ZERO;
        Optional<ShoppingDiscount> discount =
                discounts.stream().
                        filter(d -> d.getProductCode().equals(listItem.getProductCode()))
                        .findFirst();
        if(discount.isPresent()){
            discountAmount = discount.get().calculateDiscountAmount(listItem);
        }
        return discountAmount;
    }

    public static BigDecimal calculateBill(String[] shoppingList) {
        ShoppingBasket basket = new ShoppingBasket();
        Arrays.stream(shoppingList).forEach( p ->  basket.addItem(new ShoppingListItem(p)));
        BigDecimal invoiceSubTotal = basket.getShoppingListItems().stream().map(basketItem -> {
            BigDecimal itemPrice = getProductPrice(basketItem.getProductCode());
            BigDecimal lineTotal = itemPrice.multiply(new BigDecimal(basketItem.getQuantity()))
                    .setScale( myNumDecimals, RoundingMode.HALF_UP);;
            return lineTotal; }).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountTotal = basket.getShoppingListItems().stream().
                map(bi -> calculateDiscountTotal(bi))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoiceSubTotal = invoiceSubTotal.subtract(discountTotal);
        return invoiceSubTotal;
    }

    public Set<ShoppingDiscount> getDiscounts() {
        return discounts;
    }

    public static void setDiscounts(Set<ShoppingDiscount> discounts){
        ShoppingTill.discounts.addAll(discounts);
    }

    public static void setProductOffering(Set<Product> catalogue) {
        ShoppingTill.PRODUCT_SET.addAll(catalogue);
    }
}

