# Implement infrastructure layer for placing an order for a drink

**Contexte**
The infrastructure layer implements the repository ports defined in the domain to persist orders and update festival goer token balances in the database. It handles database transactions and ensures data consistency.

**Critères d'acceptation**
Feature: Order and FestivalGoer Repository
  Scenario: Save order to database
    Given an order entity
    When the saveOrder method is called
    Then the order is persisted in the database

  Scenario: Update festival goer balance
    Given a festival goer with updated token balances
    When the updateBalance method is called
    Then the balances are updated in the database

  Scenario: Retrieve festival goer for balance check
    Given a festival goer ID
    When findById is called
    Then return the festival goer entity with current balances

  Scenario: Festival goer not found
    Given a non-existent festival goer ID
    When findById is called
    Then return null

**Notes**
- Use JPA entities for Order and FestivalGoer.
- Ensure transactional integrity for order placement.
- Implement FestivalGoerRepositoryPort and OrderRepositoryPort.