# Implement domain logic for marking an order as ready

**Contexte**
The domain layer must allow marking order as ready if all items prepared, notifying goer.

**Critères d'acceptation**
Feature: Mark Order Ready
  Scenario: Mark ready when all prepared
    Given acknowledged order with all items prepared
    When marking ready
    Then status ready, goer notified

  Scenario: Not all prepared
    Given order with unprepared items
    When marking ready
    Then error

**Notes**
- Check preparation status.