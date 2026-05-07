# Implement domain logic for acknowledging an order

**Contexte**
The domain layer must allow bartender to acknowledge an order, calculating estimated readiness time based on items, notifying the goer.

**Critères d'acceptation**
Feature: Acknowledge Order
  Scenario: Acknowledge order with drinks only
    Given order with non-alcoholic drinks
    When acknowledging
    Then estimated time calculated, goer notified, order acknowledged

  Scenario: Acknowledge order with mixed items
    Given order with drinks and food
    When acknowledging
    Then time based on rules, notified

**Notes**
- Time calculation as per rules.