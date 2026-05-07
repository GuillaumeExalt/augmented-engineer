# Implement infrastructure layer for ordering multiple items in a single order

**Contexte**
The infrastructure layer implements the repository ports to persist the order with multiple items and update token balances. It ensures transactional consistency for the entire order.

**Critères d'acceptation**
Feature: Order and FestivalGoer Repository
  Scenario: Save order with multiple items
    Given an order entity with multiple items
    When saveOrder is called
    Then the order and items are persisted

  Scenario: Update balances after order
    Given festival goer with deducted tokens
    When updateBalance is called
    Then balances are updated

  Scenario: Retrieve festival goer
    Given ID
    When findById is called
    Then return entity

**Notes**
- Use JPA with relationships for order items.
- Transactional for consistency.