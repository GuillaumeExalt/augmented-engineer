# Implement domain logic for placing an order for food

**Contexte**
The domain layer must provide the business logic to place an order for food, including validating the festival goer's snack token balance, deducting tokens based on food type (snack: 1, meal: 3), and creating an order entity. It must ensure tokens do not go negative and handle insufficient balance scenarios.

**Critères d'acceptation**
Feature: Place Food Order
  Scenario: Place order for a snack
    Given a festival goer with 5 snack tokens
    When placing an order for a snack
    Then the order is created successfully and snack tokens become 4

  Scenario: Place order for a meal
    Given a festival goer with 5 snack tokens
    When placing an order for a meal
    Then the order is created successfully and snack tokens become 2

  Scenario: Insufficient tokens for a meal
    Given a festival goer with 2 snack tokens
    When placing an order for a meal
    Then an error is thrown indicating insufficient snack tokens

**Notes**
- Food types: snack (costs 1 snack token), meal (costs 3 snack tokens).
- Tokens cannot be negative.
- Orders must be associated with the festival goer.