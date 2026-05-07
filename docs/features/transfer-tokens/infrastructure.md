# Implement infrastructure layer for transferring tokens

**Contexte**
The infrastructure layer updates balances for both goers.

**Critères d'acceptation**
Feature: FestivalGoer Repository
  Scenario: Update sender balance
    Given sender
    When deduct
    Then updated

  Scenario: Update recipient balance
    Given recipient
    When add
    Then updated

**Notes**
- Transactional.