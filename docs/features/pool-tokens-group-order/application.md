# Implement application layer for pooling tokens to place a group order

**Contexte**
The application layer exposes an API for group orders, handling the list of contributors and their contributions, validating inputs, and processing the order.

**Critères d'acceptation**
Feature: Pool Tokens Group Order API
  Scenario: Successful group order
    Given POST /group-orders with contributors and items
    And sufficient pooled tokens
    When processed
    Then return 201 with order ID

  Scenario: Insufficient pooled tokens
    Given POST /group-orders
    And insufficient total tokens
    When processed
    Then return 400 with error

  Scenario: Invalid contributor
    Given POST with non-existent goer
    When processed
    Then return 400

**Notes**
- Use GroupOrderRequestDto.