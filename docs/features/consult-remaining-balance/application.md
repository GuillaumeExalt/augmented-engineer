# Implement REST API endpoint for consulting token balance

**Contexte**
As a festival goer, I want to consult the remaining balance of my tokens via a REST API. This involves creating a controller in the application layer to handle GET requests for token balance, mapping to the domain use case, and returning appropriate DTOs.

**Critères d'acceptation**
Feature: Consult token balance API
  Scenario: Successful API call
    Given a festival goer with ID 123
    When I send a GET request to /festival-goers/123/balance
    Then I receive a 200 response with drinkTokens: 5, snackTokens: 3

  Scenario: Festival goer not found
    Given a festival goer with ID 999 does not exist
    When I send a GET request to /festival-goers/999/balance
    Then I receive a 404 response

  Scenario: Invalid request
    Given an invalid festival goer ID
    When I send a GET request to /festival-goers/invalid/balance
    Then I receive a 400 response

**Notes**
- Use appropriate HTTP status codes.
- Validate input parameters.
- Map domain objects to DTOs for response.