# Implement repository for festival goer data persistence

**Contexte**
To support consulting token balance, the infrastructure layer needs to implement the FestivalGoerRepository port defined in the domain. This involves creating a persistence mechanism (e.g., JPA entity, repository) to store and retrieve festival goer information including token balances.

**Critères d'acceptation**
Feature: Festival goer repository
  Scenario: Retrieve existing festival goer
    Given a festival goer with ID 123 exists in the database
    When the repository fetches the festival goer by ID
    Then it returns the festival goer with correct token balances

  Scenario: Festival goer not found
    Given a festival goer with ID 999 does not exist
    When the repository fetches the festival goer by ID
    Then it returns null or throws NotFoundException

  Scenario: Save festival goer
    Given a new festival goer with tokens
    When the repository saves the festival goer
    Then the festival goer is persisted with correct data

**Notes**
- Implement using JPA or similar.
- Ensure data integrity and constraints.
- Handle database transactions appropriately.