# Implement domain logic for consulting festival goer token balance

**Contexte**
As a festival goer, I want to consult the remaining balance of my tokens. This requires defining the FestivalGoer entity with drink and snack token balances, value objects for tokens, and a use case to retrieve the balance. The domain layer must enforce rules like non-negative balances and daily token allocation.

**Critères d'acceptation**
Feature: Consult token balance
  Scenario: Successful balance retrieval
    Given a festival goer with 5 drink tokens and 3 snack tokens
    When the festival goer requests their token balance
    Then the system returns 5 drink tokens and 3 snack tokens

  Scenario: Zero tokens balance
    Given a festival goer with 0 drink tokens and 0 snack tokens
    When the festival goer requests their token balance
    Then the system returns 0 drink tokens and 0 snack tokens

  Scenario: Prevent negative tokens
    Given a festival goer attempts to have negative tokens
    When the system validates the balance
    Then an error is raised indicating invalid balance

**Notes**
- Tokens are allocated daily: 6 drink tokens and 9 snack tokens.
- Unspent tokens do not carry over to the next day.
- Ensure immutability of value objects.