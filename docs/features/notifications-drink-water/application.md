# Implement application layer for notifications

**Contexte**
The application layer may have a scheduled task or endpoint to trigger notifications.

**Critères d'acceptation**
Feature: Notifications API
  Scenario: Trigger notification
    Given scheduled or manual trigger
    When processing
    Then notifications sent based on rules

**Notes**
- Perhaps a background service.