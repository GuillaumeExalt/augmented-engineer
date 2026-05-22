package com.it.exalt.belair.application.order.model.in;

import java.util.List;

public record ModifyDrinkOrderRequest(
        List<ArticleChange> ajouts,
        List<ArticleChange> retraits,
        int soldeJetonsBoissonDisponibles,
        int soldeJetonsNourritureDisponibles
) {
    public record ArticleChange(String id, int quantite) {
    }
}
