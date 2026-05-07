# Implement application layer for acknowledging an order

**Contexte**
The application layer exposes API for bartender to acknowledge order.

**Critères d'acceptation**
Feature: Acknowledge Order API
  Scenario: Successful acknowledge
    Given POST /orders/{id}/acknowledge
    When processed
    Then return 200 with estimated time

**Notes**
- Bartender endpoint.