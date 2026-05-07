# Implement infrastructure layer for sending notifications

**Contexte**
The infrastructure layer sends notifications via email, push, etc.

**Critères d'acceptation**
Feature: Notification Service
  Scenario: Send to all goers
    Given message
    When send
    Then all notified

  Scenario: Send to specific goer
    Given goer and message
    When send
    Then notified

**Notes**
- Integration with notification provider.