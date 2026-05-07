# Implement domain logic for canceling an order

**Contexte**
The domain layer must allow canceling an order only if not acknowledged, refunding the tokens used.

**Critères d'acceptation**
Feature: Cancel Order
  Scenario: Cancel unacknowledged order
    Given unacknowledged order
    When canceling
    Then order canceled, tokens refunded, confirmation sent

  Scenario: Cancel acknowledged order
    Given acknowledged order
    When canceling
    Then error, cannot cancel

**Notes**
- Refund all tokens used in the order.