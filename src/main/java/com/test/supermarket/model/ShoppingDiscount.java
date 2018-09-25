package com.test.supermarket.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.test.supermarket.basket.ShoppingBasket;

/**
 * @author kay
 *encapsulates discount that may be applied to a product based on number items purchased
 */
public class ShoppingDiscount {

    private double discountAmount;
    private String productCode;
    private double triggerQuantity;
    private String discountDescription;

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public double getTriggerQuantity() {
        return triggerQuantity;
    }

    public void setTriggerQuantity(double triggerQuantity) {
        this.triggerQuantity = triggerQuantity;
    }

    /**
     * calculates discount that may apply to specific items purchased
     * @param item
     * @return amount of discount
     */
    public BigDecimal calculateDiscountAmount(ShoppingListItem item){
        int myNumDecimals = 2;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        double orderQty = item.getQuantity();
        if(orderQty >= triggerQuantity){
            BigDecimal price = ShoppingBasket.getProductPrice(productCode);
            if(orderQty % triggerQuantity == 0){
                totalDiscount = (new BigDecimal(orderQty).multiply(price)).multiply(new BigDecimal(discountAmount)).
                        setScale( myNumDecimals, RoundingMode.HALF_UP);
            }else{
                double surplus = orderQty % triggerQuantity;
                double qtyEligibleForDiscount = orderQty - surplus;
                BigDecimal discountableAmount = price.multiply(new BigDecimal(qtyEligibleForDiscount));
                totalDiscount = discountableAmount.multiply(new BigDecimal(discountAmount)).
                        setScale( myNumDecimals, RoundingMode.HALF_UP);
            }
        }
        return totalDiscount;
    }

    @Override
    public boolean equals(Object o) {
        ShoppingDiscount toBeCompared = (ShoppingDiscount)o;
        return productCode.equals(toBeCompared.getProductCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode);
    }

}

