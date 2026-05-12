---
agent: agent
name: TDD Refactor step
description: "Use when you want to refactor only one layer and only the selected scenario numbers from a layer-scoped Green result: move production-worthy code out of tests for that slice, keep behavior unchanged, and keep the targeted tests green after each micro-step."
argument-hint: "Refactor one layer and one selected scenario slice from the Green result: green_step_result_json={green_step_result_json}"
tools: ['execute/getTerminalOutput', 'execute/runInTerminal', 'read/problems', 'read/readFile', 'read/terminalSelection', 'read/terminalLastCommand', 'edit/createDirectory', 'edit/createFile', 'edit/editFiles', 'search', 'todo']
model: GPT-5.4 (copilot)
---

# Refactor TDD step prompt

## Goal
Refactor exactly one layer and only the selected scenario numbers already made green.

The prompt must:
- consume a Green JSON result that is already scoped to one layer
- move only production-worthy code for that layer and scenario slice
- keep pure test scaffolding in tests
- validate after every micro-step with the narrowest relevant tests
- keep behavior unchanged

## Expected input

Input:
- `green_step_result_json`: the JSON output of the layer-scoped Green step

The consumed Green JSON must contain at least:
- `status`
- `issueReference`
- `resolvedIssue`
- `requestedLayer`
- `scenarioNumbers`
- `targetClass`
- `selectedTestMethods`
- `modifiedFiles`
- `implementationLocation`
- `testResults`

## Validation rules
- If `status` is not `GREEN`, return `BLOCKED`.
- If the Green result spans more than one layer, return `BLOCKED`.
- If the Green result does not expose `requestedLayer` and `scenarioNumbers`, return `BLOCKED`.
- If the target class was not `PASSED` in Green, return `BLOCKED`.

## Single-layer refactor rule
- Refactor only the `requestedLayer` from the Green result.
- Do not create production code in another layer during this prompt.
- If the requested layer needs a missing dependency from another layer to be refactored first, stop and return `BLOCKED`.

## Layer routing rules
- `application`:
  - move transport entrypoints under `controller`
  - move request DTOs under `model/in`
  - move response DTOs under `model/out`
  - keep the controller focused on transport mapping and delegation
  - do not create domain or infrastructure production code here
- `domain`:
  - move business logic under `usecase`
  - move domain models, commands, results, enums, and typed business exceptions under `model`
  - move repository interfaces under `port/out`
  - do not create concrete infrastructure implementations here
- `infrastructure`:
  - move concrete adapters and repositories under `repository`
  - move technical inputs under `model/in`
  - move technical outputs under `model/out`
  - move persistence entities or records under `model/entity` or `model/persistence`
  - move mapping code under `mapper`
  - do not create application or domain business code here

## Instructions
1. Parse the provided Green JSON.
2. Confirm that it is a single-layer Green result.
3. Read the selected target test file and only the symbols introduced for that slice.
4. Decide which symbols are production-worthy and which symbols must remain test-only.
5. Perform one micro-step at a time:
   - create or update one production symbol
   - switch the test to use it
   - run the narrowest affected validation
   - remove obsolete test-only duplication if safe
   - rerun the same validation
6. Finish by running the full targeted class for that layer.
7. Return only valid JSON.

## Requirements
- You **MUST** keep the refactor scoped to the Green layer and scenario slice.
- You **MUST** preserve the selected test method names exactly.
- You **MUST** keep behavior unchanged.
- You **MUST** move only production-worthy code out of tests.
- You **MUST** keep test-only doubles, fixtures, and scaffolding in test sources.
- You **MUST** validate after every micro-step when a relevant test exists.
- You **MUST NOT** add new scenarios.
- You **MUST NOT** widen the refactor to another layer.
- You **MUST NOT** create cross-layer production code just because it would be convenient.

## Hard stops
- Do not refactor the whole feature if Green only targeted one scenario slice.
- Do not create domain production classes from an application refactor slice.
- Do not create infrastructure implementations from a domain refactor slice.
- Do not move business validation rules into controllers.
- Do not move technical persistence concerns into the domain.
- Do not change assertions or expected values in the selected tests.
- Do not stop at a compile-only check. Run the tests.

## JSON output contract

Return a single valid JSON object with this shape:

```json
{
  "status": "REFACTORED | BLOCKED",
  "consumedGreenStatus": "string",
  "issueReference": "string",
  "resolvedIssue": "string",
  "requestedLayer": "application | domain | infrastructure",
  "scenarioNumbers": [1],
  "targetClass": {
    "layer": "application | domain | infrastructure",
    "testFilePath": "string",
    "testClassName": "string",
    "status": "PASSED | FAILED | NOT_RUN | BLOCKED"
  },
  "selectedTestMethods": ["string"],
  "modifiedFiles": ["string"],
  "microSteps": ["string"],
  "movedProductionSymbols": [
    {
      "symbol": "string",
      "from": "string",
      "to": "string"
    }
  ],
  "remainingTestOnlySymbols": [
    {
      "filePath": "string",
      "symbols": ["string"]
    }
  ],
  "executedTests": ["string"],
  "testResults": {
    "<testClassName>": {
      "<testMethodName>": "PASSED | FAILED | NOT_RUN"
    }
  },
  "notes": ["string"]
}
```

## Example output
```json
{
  "status": "REFACTORED",
  "consumedGreenStatus": "GREEN",
  "issueReference": "passer-commande-boisson",
  "resolvedIssue": "docs/features/passer-commande-boisson/application_passer-commande-boisson.md",
  "requestedLayer": "application",
  "scenarioNumbers": [4, 5, 6],
  "targetClass": {
    "layer": "application",
    "testFilePath": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
    "testClassName": "com.example.application.order.PlaceDrinkOrderControllerTest",
    "status": "PASSED"
  },
  "selectedTestMethods": [
    "shouldReturnPendingOrderIdentifierWhenSubmittingAvailableDrinkOrderScenario4",
    "shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5",
    "shouldReturnBadRequestWhenSubmittingAvailableDrinkOrderWithoutRequestedArticleScenario6"
  ],
  "modifiedFiles": [
    "application/src/main/java/com/example/application/order/controller/PlaceDrinkOrderController.java",
    "application/src/main/java/com/example/application/order/model/in/CreateDrinkOrderRequest.java",
    "application/src/main/java/com/example/application/order/model/out/CreateDrinkOrderResponse.java",
    "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java"
  ],
  "microSteps": [
    "Created the production controller for the selected application scenario slice.",
    "Moved the request DTO for the selected application scenario slice.",
    "Moved the response DTO for the selected application scenario slice.",
    "Removed the obsolete temporary controller code from the test class."
  ],
  "movedProductionSymbols": [
    {
      "symbol": "PlaceDrinkOrderController",
      "from": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
      "to": "application/src/main/java/com/example/application/order/controller/PlaceDrinkOrderController.java"
    }
  ],
  "remainingTestOnlySymbols": [
    {
      "filePath": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
      "symbols": ["RecordingPlaceDrinkOrderUseCase"]
    }
  ],
  "executedTests": [
    "./gradlew :application:test --tests com.example.application.order.PlaceDrinkOrderControllerTest"
  ],
  "testResults": {
    "com.example.application.order.PlaceDrinkOrderControllerTest": {
      "shouldReturnPendingOrderIdentifierWhenSubmittingAvailableDrinkOrderScenario4": "PASSED",
      "shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5": "PASSED",
      "shouldReturnBadRequestWhenSubmittingAvailableDrinkOrderWithoutRequestedArticleScenario6": "PASSED"
    }
  },
  "notes": [
    "The refactor stayed inside the application layer.",
    "Behavior remained unchanged for the selected scenario slice."
  ]
}
```