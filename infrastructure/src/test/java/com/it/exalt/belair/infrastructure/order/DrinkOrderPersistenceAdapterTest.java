package com.it.exalt.belair.infrastructure.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DrinkOrderPersistenceAdapterTest {

    @Test
    void shouldReturnDrinkCategoryWhenLoadingCatalogArticleMetadataScenario1() {
        // Given
        DrinkOrderPersistenceAdapter adapter = new DrinkOrderPersistenceAdapter();

        // When
        CatalogDrinkArticle article = adapter.loadCatalogArticle("article-1");

        // Then
        assertAll(
                () -> assertEquals("article-1", article.id()),
                () -> assertEquals(DrinkCategory.ALCOHOL_STANDARD, article.category())
        );
    }

    @Test
    void shouldPersistOrderAndDrinkTokenDebitInSameTransactionWhenSavingValidatedOrderScenario2() {
        // Given
        ValidatedDrinkOrder order = new ValidatedDrinkOrder("order-1", "festivalier-42", DrinkCategory.ALCOHOL_STANDARD, 1);
        DrinkOrderPersistenceAdapter adapter = new DrinkOrderPersistenceAdapter();

        // When
        PersistenceOutcome outcome = adapter.save(order);

        // Then
        assertAll(
                () -> assertEquals(PersistenceStatus.COMMITTED, outcome.status()),
                () -> assertEquals(1, outcome.savedOrdersCount()),
                () -> assertEquals(1, outcome.savedDrinkTokenDebitsCount())
        );
    }

    @Test
    void shouldRollbackDrinkTokenDebitWhenSavingValidatedOrderFailsTechnicallyScenario3() {
        // Given
        ValidatedDrinkOrder order = new ValidatedDrinkOrder("order-2", "festivalier-42", DrinkCategory.ALCOHOL_STANDARD, 1);
        DrinkOrderPersistenceAdapter adapter = new DrinkOrderPersistenceAdapter();

        // When
        PersistenceOutcome outcome = adapter.saveWithTechnicalFailure(order);

        // Then
        assertAll(
                () -> assertEquals(PersistenceStatus.ROLLED_BACK, outcome.status()),
                () -> assertEquals(0, outcome.savedDrinkTokenDebitsCount()),
                () -> assertEquals("TECHNICAL_ERROR", outcome.errorCode())
        );
    }

    @Test
    void shouldPersistPendingOrderWithIdentifierWhenCreatingOrderForAvailableArticleScenario4() {
        // Given
        CreatePendingDrinkOrderRequest request = new CreatePendingDrinkOrderRequest("festivalier-42", "article-1", "Mojito", 1, true);
        DrinkOrderPersistenceAdapter adapter = new DrinkOrderPersistenceAdapter();

        // When
        PersistedDrinkOrder order = adapter.createPendingOrder(request);

        // Then
        assertAll(
                () -> assertEquals(OrderStatus.EN_ATTENTE, order.status()),
                () -> assertNotNull(order.id()),
                () -> assertFalse(order.id().isBlank())
        );
    }
}