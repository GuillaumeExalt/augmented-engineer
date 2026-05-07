# Implement application layer for canceling an order

**Contexte**
The application layer exposes API to cancel order.

**Critères d'acceptation**
Feature: Cancel Order API
  Scenario: Successful cancel
    Given DELETE /orders/{id}
    And order unacknowledged
    When processed
    Then return 200 with confirmation

  Scenario: Order acknowledged
    Given DELETE for acknowledged order
    When processed
    Then return 400 "Cannot cancel acknowledged order"

**Notes**
- Return confirmation message.