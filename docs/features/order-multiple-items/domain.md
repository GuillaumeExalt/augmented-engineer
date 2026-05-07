# Implement domain logic for ordering multiple items in a single order

**Contexte**
The domain layer must provide the business logic to place an order containing multiple items (drinks and food), calculating the total token cost for drink and snack tokens separately, validating that the festival goer has sufficient balances for both types, deducting tokens accordingly, and creating a single order entity with all items.

**Critères d'acceptation**
Feature: Order Multiple Items
  Scenario: Order multiple drinks and food with sufficient tokens
    Given a festival goer with 5 drink tokens and 5 snack tokens
    When placing an order for 2 normal alcoholic drinks and 1 snack
    Then the order is created successfully, drink tokens become 3, snack tokens become 4

  Scenario: Insufficient drink tokens for multiple items
    Given a festival goer with 1 drink token and 5 snack tokens
    When placing an order for 2 normal alcoholic drinks and 1 snack
    Then an error is thrown indicating insufficient drink tokens

  Scenario: Insufficient snack tokens for multiple items
    Given a festival goer with 5 drink tokens and 1 snack token
    When placing an order for 1 normal alcoholic drink and 1 meal
    Then an error is thrown indicating insufficient snack tokens

  Scenario: Place order with only non-alcoholic drinks and no token cost
    Given a festival goer with 0 drink tokens and 0 snack tokens
    When placing an order with two non-alcoholic drinks and no food items
    Then the order is created successfully with no token deduction

  Scenario: Empty order
    Given a festival goer
    When placing an order with no items
    Then an error is thrown indicating order must have items

**Notes**
- Total cost must not exceed balances for both token types.
- Deduct only after validation.
- Items can be mixed drinks and food.