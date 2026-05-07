# Implement infrastructure layer for changing an order

**Contexte**
The infrastructure layer updates the order in database and adjusts balances.

**Critères d'acceptation**
Feature: Order Repository
  Scenario: Update order
    Given modified order
    When save
    Then updated

  Scenario: Update balance
    Given goer
    When update
    Then balance updated

**Notes**
- Transactional.