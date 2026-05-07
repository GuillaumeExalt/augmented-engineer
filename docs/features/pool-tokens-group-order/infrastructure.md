# Implement infrastructure layer for pooling tokens to place a group order

**Contexte**
The infrastructure layer persists the group order and updates multiple festival goers' balances transactionally.

**Critères d'acceptation**
Feature: Group Order Repository
  Scenario: Save group order
    Given group order entity
    When save
    Then persisted

  Scenario: Update multiple balances
    Given list of goers with new balances
    When update
    Then all updated

**Notes**
- Transactional to ensure all or none.