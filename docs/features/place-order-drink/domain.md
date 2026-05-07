# Implement domain logic for placing an order for a drink

**Contexte**
The domain layer must provide the business logic to place an order for a drink, including validating the festival goer's token balance, deducting the appropriate number of drink tokens based on the drink type (non-alcoholic: 0, normal alcoholic: 1, premium alcoholic: 2), and creating an order entity. It must ensure tokens do not go negative and handle insufficient balance scenarios.

**Critères d'acceptation**
Feature: Place Drink Order
  Scenario: Place order for non-alcoholic drink
    Given a festival goer with 5 drink tokens
    When placing an order for a non-alcoholic drink
    Then the order is created successfully and drink tokens remain 5

  Scenario: Place order for normal alcoholic drink
    Given a festival goer with 5 drink tokens
    When placing an order for a normal alcoholic drink
    Then the order is created successfully and drink tokens become 4

  Scenario: Place order for premium alcoholic drink
    Given a festival goer with 5 drink tokens
    When placing an order for a premium alcoholic drink
    Then the order is created successfully and drink tokens become 3

  Scenario: Insufficient tokens for normal alcoholic drink
    Given a festival goer with 0 drink tokens
    When placing an order for a normal alcoholic drink
    Then an error is thrown indicating insufficient drink tokens

  Scenario: Insufficient tokens for premium alcoholic drink
    Given a festival goer with 1 drink token
    When placing an order for a premium alcoholic drink
    Then an error is thrown indicating insufficient drink tokens

**Notes**
- Drink types: non-alcoholic (costs 0 drink tokens), normal alcoholic (costs 1 drink token), premium alcoholic (costs 2 drink tokens).
- Tokens cannot be negative.
- Orders must be associated with the festival goer.