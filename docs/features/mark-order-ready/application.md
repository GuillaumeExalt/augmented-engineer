# Implement application layer for marking an order as ready

**Contexte**
The application layer exposes API for bartender to mark order ready.

**Critères d'acceptation**
Feature: Mark Order Ready API
  Scenario: Successful mark ready
    Given POST /orders/{id}/ready
    And all prepared
    When processed
    Then return 200

  Scenario: Not ready
    Given POST
    And not all prepared
    When processed
    Then return 400

**Notes**
- Bartender endpoint.