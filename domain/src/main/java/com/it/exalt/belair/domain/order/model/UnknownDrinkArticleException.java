package com.it.exalt.belair.domain.order.model;

public final class UnknownDrinkArticleException extends IllegalArgumentException {
    public UnknownDrinkArticleException() {
        super("ARTICLE_INCONNU");
    }
}