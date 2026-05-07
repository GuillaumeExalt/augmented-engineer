# Implement application layer for transferring tokens

**Contexte**
The application layer exposes API for token transfer, handling request and confirmation.

**Critères d'acceptation**
Feature: Transfer Tokens API
  Scenario: Initiate transfer
    Given POST /transfers with details
    When processed
    Then return 200, pending confirmation

  Scenario: Confirm transfer
    Given POST /transfers/{id}/confirm
    When processed
    Then transfer completed

  Scenario: Reject transfer
    Given POST /reject
    When processed
    Then canceled

**Notes**
- Two-step process.