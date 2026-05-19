package com.it.exalt.belair.domain.order.model;

public final class InsufficientFestivalierTokenBalanceException extends IllegalStateException {
    public InsufficientFestivalierTokenBalanceException() {
        super("SOLDE_JETONS_INSUFFISANT");
    }
}
