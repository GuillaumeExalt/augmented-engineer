# Implement infrastructure layer for acknowledging an order

**Contexte**
The infrastructure layer updates order status and sends notification.

**Critères d'acceptation**
Feature: Order Repository and Notification
  Scenario: Update order status
    Given order
    When acknowledge
    Then status set to acknowledged

  Scenario: Send notification
    Given goer
    When notify
    Then notification sent

**Notes**
- Notification service.