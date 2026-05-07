# Implement application layer for placing an order for food

**Contexte**
The application layer exposes a REST API endpoint to allow festival goers to place an order for food. It handles input validation, maps the request DTO to domain commands, invokes the use case, and formats the response DTO. It must handle errors like insufficient tokens and invalid inputs.

**Critères d'acceptation**
Feature: Place Food Order API
  Scenario: Successful order placement for snack
    Given a POST request to /orders with valid food order details for a snack
    And the festival goer has sufficient tokens
    When the request is processed
    Then return HTTP 201 with the created order ID

  Scenario: Successful order placement for meal
    Given a POST request to /orders with valid food order details for a meal
    And the festival goer has sufficient tokens
    When the request is processed
    Then return HTTP 201 with the created order ID and updated token balance

  Scenario: Insufficient tokens
    Given a POST request to /orders with food order details
    And the festival goer has insufficient snack tokens
    When the request is processed
    Then return HTTP 400 with error message "Insufficient snack tokens"

  Scenario: Invalid food type
    Given a POST request to /orders with invalid food type
    When the request is processed
    Then return HTTP 400 with error message "Invalid food type"

**Notes**
- Use OrderRequestDto for input, OrderResponseDto for output.
- Validate request parameters.
- Map domain exceptions to appropriate HTTP status codes.