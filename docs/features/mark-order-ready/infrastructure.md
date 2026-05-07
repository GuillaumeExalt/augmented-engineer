# Implement infrastructure layer for marking an order as ready

**Contexte**
The infrastructure layer updates status and notifies.

**Critères d'acceptation**
Feature: Order Repository
  Scenario: Update to ready
    Given order
    When mark ready
    Then status ready

  Scenario: Notify goer
    Given goer
    When notify
    Then sent

**Notes**
- Notification.