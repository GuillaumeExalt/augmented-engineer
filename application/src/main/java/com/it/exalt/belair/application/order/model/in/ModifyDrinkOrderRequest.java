package com.it.exalt.belair.application.order.model.in;

import java.util.List;

public record ModifyDrinkOrderRequest(
        List<LineChange> ajouts,
        List<LineChange> retraits,
        TokenValues soldes,
        TokenValues couts
) {
    public record LineChange(String article, int quantite) {
    }

    public record TokenValues(int boisson, int nourriture) {
    }
}
