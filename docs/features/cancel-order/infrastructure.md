# Implement infrastructure layer for canceling an order

**Contexte**
The infrastructure layer marks order as canceled and refunds balances.

**Critères d'acceptation**
Feature: Order Repository
  Scenario: Cancel order
    Given order
    When cancel
    Then status updated to canceled

  Scenario: Refund balance
    Given goer
    When update balance
    Then tokens added back

**Notes**
- Transactional.