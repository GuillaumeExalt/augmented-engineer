---
agent: agent
name: TDD Green step
description: "Use when you want to make one layer green from a Red-step JSON result: consume one Red output object, target only the resolved layer and scenario numbers, implement only that slice in test code, and return JSON for a layer-scoped Refactor step."
argument-hint: 'Make the Red result green for one layer: { "status": "RED", ... }'
tools: ['execute/getTerminalOutput', 'execute/runInTerminal', 'read/problems', 'read/readFile', 'read/terminalSelection', 'read/terminalLastCommand', 'edit/createDirectory', 'edit/createFile', 'edit/editFiles', 'search', 'todo']
model: GPT-5.4 (copilot)
---

# Green TDD step prompt

## Goal
Handle exactly one layer and only the requested scenario numbers.

For the requested layer only, the prompt must:
- consume one Red-step JSON object
- resolve the correct split issue file from `resolvedIssueFiles + requestedLayer`
- extract only the `selectedScenarioNumbers`
- start from the exact `targetTestClass` selected by the Red step
- make only the selected scenarios green
- keep the implementation inside test code
- return JSON that is already scoped to one layer and one set of scenario numbers

## Expected input

Input:
- one JSON object matching the Red-step output contract

Required input fields:
- `status` with value `RED`
- `requestedLayer`
- `featureFolder`
- `resolvedIssueFiles`
- `selectedScenarioNumbers`
- `targetTestClass`

Accepted input forms:
- a raw JSON object
- a fenced JSON block
- ` {...}`

Examples:
```json
{
  "status": "RED",
  "requestedLayer": "domain",
  "featureFolder": "docs/features/passer-commande-boisson",
  "resolvedIssueFiles": [
    "docs/features/passer-commande-boisson/application_passer-commande-boisson.md",
    "docs/features/passer-commande-boisson/domain_passer-commande-boisson.md",
    "docs/features/passer-commande-boisson/infrastructure_passer-commande-boisson.md"
  ],
  "selectedScenarioNumbers": [4],
  "targetTestClass": {
    "layer": "domain",
    "testFilePath": "domain/src/test/java/com/it/exalt/belair/domain/commande/PasserCommandeBoissonUseCaseTest.java",
    "testClassName": "com.it.exalt.belair.domain.commande.PasserCommandeBoissonUseCaseTest",
    "status": "FAILED"
  }
}
```

## Resolution rules
- If `status` is not `RED`, stop and return `BLOCKED`.
- Resolve the layer-specific split issue file by selecting the path from `resolvedIssueFiles` that matches `requestedLayer`.
- If no issue file matches `requestedLayer`, stop and return `BLOCKED`.
- Use `selectedScenarioNumbers` as the only scenario numbers in scope for this Green step.
- If a selected scenario number does not exist in the resolved issue file, stop and return `BLOCKED`.
- Start from `targetTestClass.testFilePath` from the Red result.
- If `targetTestClass.testFilePath` does not exist anymore, stop and return `BLOCKED`.
- If no Red-step test method exists in that class for a selected scenario number, stop and return `BLOCKED`.

## Selection rules
- Read the full target test file from `targetTestClass.testFilePath`.
- Identify only the tests that map to the requested `ScenarioN` numbers.
- Ignore other scenarios in the same feature unless a shared helper must be adjusted to make the selected tests pass.
- Shared helper edits are allowed only inside the same requested-layer test file or the same test source set.
- Do not make unrelated scenario numbers green on purpose.

## Instructions
1. Parse the Red-step JSON input.
2. Validate that `status == RED` and determine `requestedLayer`.
3. Resolve the exact split issue file by matching `requestedLayer` against `resolvedIssueFiles`.
4. Use `selectedScenarioNumbers` as the only scenario numbers in scope.
5. Verify that `targetTestClass.testFilePath` exists, then read the full test file.
6. Identify the requested `ScenarioN` test methods in `targetTestClass.testClassName`.
7. Rerun the narrowest possible validation from the Red result, or a stricter selector when needed, to confirm the targeted tests are still red.
8. Implement the minimum test-local code needed to make only those selected scenarios pass.
9. Rerun the same narrow validation.
10. Return only valid JSON.

## Requirements
- You **MUST** consume a single Red-step JSON result and reject non-`RED` inputs.
- You **MUST** work on one layer only.
- You **MUST** work on only the requested scenario numbers from `selectedScenarioNumbers`.
- You **MUST NOT** modify production code in this step.
- You **MUST NOT** implement scenarios that were not requested.
- You **MUST NOT** rename the selected test methods.
- You **MUST NOT** invent new `ScenarioN` suffixes.
- You **MUST** keep the implementation in test code.
- You **MUST** use `targetTestClass` from the Red result as the ownership anchor for the Green step.
- You **MUST** prefer the existing business concept and existing test class when present.
- You **MUST** follow the testing instructions for the requested layer.

## Layer guard rails
- `application`: keep a controller-to-domain seam inside the test code. Do not embed domain business rules in the temporary controller.
- `domain`: keep a use-case-to-port seam inside the test code. Do not create concrete infrastructure implementations.
- `infrastructure`: keep the implementation aligned with the existing domain port contract. Do not move business validation rules into the repository.

## Hard stops
- Do not refactor during Green.
- Do not update another layer.
- Do not ignore the `targetTestClass` selected by the Red step to create a parallel test class when that file still exists.
- Do not reinterpret the feature family when `featureFolder` and `resolvedIssueFiles` already resolved it during Red.
- Do not widen the implementation to all scenarios in the class if only a subset was requested.
- Do not create production classes under `src/main`.
- Do not create a parallel top-level concept when an equivalent one already exists.

## JSON output contract

Return a single valid JSON object with this shape:

```json
{
  "status": "GREEN | PARTIAL | BLOCKED",
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
  "implementationLocation": [
    {
      "filePath": "string",
      "scope": "test-class | same-test-file | test-source-set",
      "symbolsAdded": ["string"]
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
  "status": "GREEN",
  "issueReference": "passer-commande-boisson",
  "resolvedIssue": "docs/features/passer-commande-boisson/application_passer-commande-boisson.md",
  "requestedLayer": "application",
  "scenarioNumbers": [5, 6],
  "targetClass": {
    "layer": "application",
    "testFilePath": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
    "testClassName": "com.example.application.order.PlaceDrinkOrderControllerTest",
    "status": "PASSED"
  },
  "selectedTestMethods": [
    "shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5",
    "shouldReturnBadRequestWhenSubmittingAvailableDrinkOrderWithoutRequestedArticleScenario6"
  ],
  "modifiedFiles": [
    "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java"
  ],
  "implementationLocation": [
    {
      "filePath": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
      "scope": "test-class",
      "symbolsAdded": ["RecordingPlaceDrinkOrderUseCase"]
    }
  ],
  "executedTests": [
    "./gradlew :application:test --tests com.example.application.order.PlaceDrinkOrderControllerTest.shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5",
    "./gradlew :application:test --tests com.example.application.order.PlaceDrinkOrderControllerTest.shouldReturnBadRequestWhenSubmittingAvailableDrinkOrderWithoutRequestedArticleScenario6"
  ],
  "testResults": {
    "com.example.application.order.PlaceDrinkOrderControllerTest": {
      "shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5": "PASSED",
      "shouldReturnBadRequestWhenSubmittingAvailableDrinkOrderWithoutRequestedArticleScenario6": "PASSED"
    }
  },
  "notes": [
    "Only the requested application scenarios were implemented.",
    "No production code was modified."
  ]
}
```