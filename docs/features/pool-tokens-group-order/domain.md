# Implement domain logic for pooling tokens to place a group order

**Contexte**
The domain layer must provide the business logic for a group of festival goers to pool their tokens and place a single order. It involves collecting contributions from each goer, validating total pooled tokens are sufficient for the order cost, deducting tokens from each contributor, and creating the group order.

**Critères d'acceptation**
Feature: Pool Tokens for Group Order
  Scenario: Successful group order with sufficient pooled tokens
    Given two festival goers with tokens
    When pooling tokens and placing order for items costing total 3 drink and 2 snack
    Then order created, tokens deducted from each

  Scenario: Insufficient pooled tokens
    Given group with total tokens less than order cost
    When placing group order
    Then error thrown

  Scenario: One goer has insufficient for their contribution
    Given a goer contributing more than they have
    When placing order
    Then error for that goer

  Scenario: Empty group
    Given no goers in group
    When placing order
    Then error

**Notes**
- Contributions are specified per goer.
- Total pooled must cover cost.
- Deduct from each.