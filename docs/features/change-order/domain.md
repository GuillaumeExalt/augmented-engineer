# Implement domain logic for changing an order

**Contexte**
The domain layer must allow changing an order only if not acknowledged, adding/removing items, validating new total cost against balances, and updating the order.

**Critères d'acceptation**
Feature: Change Order
  Scenario: Add item to unacknowledged order
    Given unacknowledged order
    When adding item with sufficient tokens
    Then order updated, tokens adjusted

  Scenario: Remove item from unacknowledged order
    Given unacknowledged order with items
    When removing item
    Then order updated, tokens refunded

  Scenario: Change acknowledged order
    Given acknowledged order
    When trying to change
    Then error, cannot change

  Scenario: Insufficient tokens for change
    Given order
    When adding item exceeding tokens
    Then error

**Notes**
- Only unacknowledged orders can be changed.
- Recalculate cost and adjust tokens.