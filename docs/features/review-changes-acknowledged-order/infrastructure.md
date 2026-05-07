# Implement infrastructure layer for reviewing changes

**Contexte**
The infrastructure layer updates order and notifies.

**Critères d'acceptation**
Feature: Order Repository
  Scenario: Apply changes
    Given approved changes
    When update
    Then order modified

  Scenario: Notify
    Given goer
    When notify
    Then sent

**Notes**
- Transactional.