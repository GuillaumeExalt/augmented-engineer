# Implement domain logic for transferring tokens

**Contexte**
The domain layer must allow transferring up to 3 tokens of each type to another goer, with confirmation.

**Critères d'acceptation**
Feature: Transfer Tokens
  Scenario: Successful transfer
    Given sender with tokens
    When transferring 2 drink to recipient
    And recipient confirms
    Then tokens transferred

  Scenario: Transfer more than 3
    Given sender
    When transferring 4
    Then error

  Scenario: Insufficient tokens
    Given sender with 1
    When transferring 2
    Then error

  Scenario: Recipient rejects
    Given transfer request
    When recipient rejects
    Then not transferred

**Notes**
- Up to 3 per type.
- Confirmation required.