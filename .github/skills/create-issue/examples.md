# Examples

## Example 1: Simple feature requiring one issue per layer

Input: "The user wants to export their contacts list to CSV"

Output:
Three files, one per module: one for the domain, one for the application, one for the infrastructure.

file `docs/features/export-contacts/domain_export-contacts-issue.md`
```markdown
# Export Contacts List : Domain Module impact
**Context**
The user wants to export their contacts list to CSV to facilitate sharing and backing up their data.

**Acceptance Criteria**
Feature: Export contacts list
    In order to share or backup contacts
    As a user
    I want to export my contacts to CSV

1. Scenario: Successfully export contacts
    Given an authenticated user with 20 contacts
    When executing a query to fetch contacts
    Then the system retrieves all 20 contacts and generates an export DTO

2. Scenario: No contacts to export
    Given an authenticated user with no contacts
    When executing a query to fetch contacts
    Then the system returns an empty export result
```

file `docs/features/export-contacts/application_export-contacts-issue.md`
```markdown
# Export Contacts List : Application Module impact
**Context**
The user wants to export their contacts list to CSV.

**Acceptance Criteria**
Feature: Export contacts list

1. Scenario: Successfully export contacts
    Given an authenticated user with 20 contacts
    When calling the GET /contacts/export endpoint with a MIME type of text/csv
    Then the application layer processes the request and returns a CSV file with all contacts

2. Scenario: No contacts to export
    Given an authenticated user with no contacts
    When calling the GET /contacts/export endpoint with a MIME type of text/csv
    Then the application layer returns a 204 No Content response
```

file `docs/features/export-contacts/infrastructure_export-contacts-issue.md`
```markdown
# Export Contacts List : Infrastructure Module impact
**Context**
The user wants to export their contacts list to CSV.

**Acceptance Criteria**
Feature: Export contacts list

1. Scenario: Transform export DTO to CSV format
    Given an export DTO containing 20 contacts
    When transforming the DTO to CSV format
    Then a valid CSV file is generated as stream of bytes with all contact details
```

## Example 2: Complex feature with multi-layer impact

Input: "A festival goer can place an order containing multiple items while ensuring the total cost does not exceed his drink or food token balance"

Output:
Three files, one for domain, one for application, one for infrastructure.

file `docs/features/order-multiple-items/domain_order-multiple-items-issue.md`
```markdown
# Order Multiple Items : Domain Module impact
**Context**
A festival goer can place an order containing multiple items while ensuring the total cost does not exceed their drink or food token balance.

**Acceptance Criteria**
Feature: Order multiple items

1. Scenario: Successfully place a multi-item order
    Given a festival goer with 3 drink tokens and 4 food tokens
    When placing an order with one alcoholic drink and one snack
    Then the order is accepted and the remaining tokens are updated correctly

2. Scenario: Reject order when drink token balance is insufficient
    Given a festival goer with 0 drink tokens and 5 food tokens
    When placing an order with one alcoholic drink
    Then the system rejects the order due to insufficient drink tokens

3. Scenario: Reject order when food token balance is insufficient
    Given a festival goer with 5 drink tokens and 0 food tokens
    When placing an order with one meal
    Then the system rejects the order due to insufficient food tokens
```

file `docs/features/order-multiple-items/application_order-multiple-items-issue.md`
```markdown
# Order Multiple Items : Application Module impact
**Context**
A festival goer can place an order containing multiple items while ensuring the total cost does not exceed their drink or food token balance.

**Acceptance Criteria**
Feature: Order multiple items

1. Scenario: Successfully submit multi-item order through API
    Given a festival goer with id 42 and valid token balances
    When calling POST /orders with two items (one drink and one snack)
    Then the application returns 201 Created and the order payload is accepted

2. Scenario: Return 400 when order exceeds drink balance
    Given a festival goer with zero drink tokens
    When calling POST /orders with a normal alcoholic drink
    Then the application returns 400 Bad Request with an insufficient drink token message

3. Scenario: Return 400 when order exceeds food balance
    Given a festival goer with zero food tokens
    When calling POST /orders with a meal
    Then the application returns 400 Bad Request with an insufficient food token message
```

file `docs/features/order-multiple-items/infrastructure_order-multiple-items-issue.md`
```markdown
# Order Multiple Items : Infrastructure Module impact
**Context**
A festival goer can place an order containing multiple items while ensuring the total cost does not exceed their drink or food token balance.

**Acceptance Criteria**
Feature: Order multiple items

1. Scenario: Persist multi-item order
    Given a validated order with one drink and one snack
    When saving the order to the database
    Then the infrastructure layer persists the order items and token usage correctly

2. Scenario: Handle missing festival goer record
    Given an order submission referencing a non-existent festival goer
    When the persistence layer looks up the festival goer
    Then it returns a not found result or throws an appropriate exception
```

## Example 3: Negative example showing what not to do

Input: "The user wants to cancel their order if it has not been acknowledged yet"

Incorrect Output: A single issue file describing only an API endpoint without splitting by impacted layers.

Why this is wrong:
- The skill must generate one issue per impacted module when a feature affects multiple layers.
- The output should not merge domain, application, and infrastructure concerns into one file.
- The title should mention the impacted module when the feature is split across modules.

Correct Output:
- `docs/features/cancel-order/domain_cancel-order-issue.md`
- `docs/features/cancel-order/application_cancel-order-issue.md`
- `docs/features/cancel-order/infrastructure_cancel-order-issue.md`

Each file should include a dedicated context and scenarios relevant to that layer.

## Example 4: Ambiguous input clarified by explicit issue splitting

Input: "Add a notification system to remind festival goers to drink water"

Expected behavior:
- Generate a domain issue for notification rules and scheduling logic.
- Generate an application issue for the notification API or scheduler trigger.
- Generate an infrastructure issue for persistence or external messaging integration.

This examples file is intentionally separate from the main skill instructions so the model can load it only when needed.