# Implement domain logic for sending notifications to drink water

**Contexte**
The domain layer must calculate when to send notifications based on time and drinking history, sending reminders.

**Critères d'acceptation**
Feature: Drink Water Notifications
  Scenario: Send hourly reminder
    Given time between 11AM-7PM
    When hour passes
    Then notification sent to all goers

  Scenario: Send more frequent if >3 alcoholic in hour
    Given goer drank 4 alcoholic
    When 30 min passes
    Then notification sent

  Scenario: No notification outside hours
    Given time 8PM
    When hour passes
    Then no notification

**Notes**
- Check drinking history.
- Friendly message.