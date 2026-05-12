package com.it.exalt.belair.domain.order.model;

public final class InsufficientDrinkStockException extends IllegalStateException {
    public InsufficientDrinkStockException() {
        super("STOCK_INSUFFISANT");
    }
}