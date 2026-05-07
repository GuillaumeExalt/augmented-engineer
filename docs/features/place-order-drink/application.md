# Implement application layer for placing an order for a drink

**Contexte**
The application layer exposes a REST API endpoint to allow festival goers to place an order for a drink. It handles input validation, maps the request DTO to domain commands, invokes the use case, and formats the response DTO. It must handle errors like insufficient tokens and invalid inputs.

**Critères d'acceptation**
Feature: Place Drink Order API
  Scenario: Successful order placement for non-alcoholic drink
    Given a POST request to /orders with valid drink order details for a non-alcoholic drink
    And the festival goer has sufficient tokens
    When the request is processed
    Then return HTTP 201 with the created order ID

  Scenario: Successful order placement for alcoholic drink
    Given a POST request to /orders with valid drink order details for an alcoholic drink
    And the festival goer has sufficient tokens
    When the request is processed
    Then return HTTP 201 with the created order ID and updated token balance

  Scenario: Insufficient tokens
    Given a POST request to /orders with drink order details
    And the festival goer has insufficient drink tokens
    When the request is processed
    Then return HTTP 400 with error message "Insufficient drink tokens"

  Scenario: Invalid drink type
    Given a POST request to /orders with invalid drink type
    When the request is processed
    Then return HTTP 400 with error message "Invalid drink type"

**Notes**
- Use OrderRequestDto for input, OrderResponseDto for output.
- Validate request parameters.
- Map domain exceptions to appropriate HTTP status codes.