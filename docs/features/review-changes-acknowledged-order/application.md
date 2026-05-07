# Implement application layer for reviewing changes

**Contexte**
The application layer exposes API for bartender to review and approve/reject changes.

**Critères d'acceptation**
Feature: Review Changes API
  Scenario: Approve
    Given POST /orders/{id}/changes/{changeId}/approve
    And can transfer
    When processed
    Then return 200 with new time

  Scenario: Reject
    Given POST /reject
    When processed
    Then return 200 rejected

**Notes**
- Bartender endpoint.