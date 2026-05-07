# Implement application layer for changing an order

**Contexte**
The application layer exposes API to change order, validating the order is changeable, processing the changes.

**Critères d'acceptation**
Feature: Change Order API
  Scenario: Successful change
    Given PUT /orders/{id} with changes
    And order unacknowledged
    When processed
    Then return 200 with updated order

  Scenario: Order acknowledged
    Given PUT for acknowledged order
    When processed
    Then return 400 "Order already acknowledged"

  Scenario: Insufficient tokens
    Given PUT with changes exceeding tokens
    When processed
    Then return 400 "Insufficient tokens"

**Notes**
- Use ChangeOrderRequestDto.