# Implement application layer for ordering multiple items in a single order

**Contexte**
The application layer exposes a REST API endpoint to allow festival goers to place an order with multiple items. It handles input validation for the list of items, maps the request DTO to domain commands, invokes the use case, and formats the response DTO. It must handle errors like insufficient tokens for any type.

**Critères d'acceptation**
Feature: Order Multiple Items API
  Scenario: Successful order placement with multiple items
    Given a POST request to /orders with a list of items (drinks and food)
    And the festival goer has sufficient tokens for all
    When the request is processed
    Then return HTTP 201 with the created order ID and updated balances

  Scenario: Insufficient tokens for drinks
    Given a POST request to /orders with items requiring drink tokens
    And insufficient drink tokens
    When the request is processed
    Then return HTTP 400 with error message "Insufficient drink tokens"

  Scenario: Insufficient tokens for snacks
    Given a POST request to /orders with items requiring snack tokens
    And insufficient snack tokens
    When the request is processed
    Then return HTTP 400 with error message "Insufficient snack tokens"

  Scenario: Invalid item in list
    Given a POST request to /orders with an invalid item type
    When the request is processed
    Then return HTTP 400 with error message "Invalid item"

**Notes**
- Use OrderRequestDto with list of items.
- Validate each item.
- Return updated balances in response.