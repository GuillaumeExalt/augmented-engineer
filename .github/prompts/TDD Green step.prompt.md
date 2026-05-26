---
agent: agent
name: TDD Green step
description: "Use when you want to make one layer green from a Red-step JSON result: consume one Red output object, target only the resolved layer and scenario numbers, implement only that slice in test code, and return JSON for a layer-scoped Refactor step. For application API slices, keep the temporary seam shaped like a future framework-backed HTTP controller instead of a plain Java object. For infrastructure persistence slices, keep the temporary seam shaped like a future JPA-backed adapter that will implement an existing or to-be-extracted pure-domain port, use separate persistence types when needed, and leave clear notes for Refactor about the required JPA entities, mapper, and wiring."
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
- for application API slices, keep the temporary seam aligned with a future framework-backed HTTP controller rather than a plain Java object invoked directly
- for infrastructure persistence slices, keep the temporary seam aligned with a future JPA promotion instead of a technology-agnostic or JDBC-oriented design
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
- For application API slices, you **MUST** note whether Refactor will need route declarations, controller annotations or handler metadata, and minimum web framework wiring for the selected scenarios.
- For application API slices, you **MUST** keep business authorization, ownership, workflow, business decisions, aggregate mutation, and repository usage out of the controller seam and in the use-case or domain seam.
- For application API slices, you **MUST** keep the seam shaped as `controller -> application use case`; the controller seam must not inject or call business repositories or ports directly.
- For application API slices, you **MUST NOT** make the controller seam load entities, inspect domain state for workflow branching, or trigger notifications directly.
- For application API slices, you **MUST** pass actor identity and ownership-relevant inputs into the use-case command or call rather than enforcing ownership in the controller seam.
- For application API slices, you **MUST** keep entity loading, ownership or authorization checks, workflow branching, and notification triggering inside the application use case or domain seam.
- For infrastructure repository slices, you **MUST** note whether the required domain port or interface already exists or must be extracted as a pure-domain contract during Refactor.
- For infrastructure persistence slices, you **MUST** note whether Refactor will need separate JPA persistence entities, a dedicated mapper, and JPA wiring for the selected scenarios.
- You **MUST** include a handoff-ready structured summary for Refactor with the exact fields `test_file_path`, `test_method_name`, and `implemented_code`.
- You **MUST** populate `implemented_code` only with class, enum, or interface symbols implemented in the target test class to make selected tests pass.
- When `status` is `GREEN`, you **MUST** set `test_file_path` and `test_method_name` to concrete values tied to a selected passing scenario test.
- When `status` is `PARTIAL` or `BLOCKED`, you **MUST** set `test_file_path` and `test_method_name` to `null`, and `implemented_code` to `[]`.

## Layer guard rails
- `application`: keep an HTTP-controller-to-domain seam inside test code. For API or endpoint scenarios, exercise a real route or web test surface and preserve HTTP method, path, status, and body behavior. Do not embed domain business rules in the temporary controller.
- `application`: before adding temporary implementation, verify the controller seam stays transport-only. Limit it to HTTP input reading, basic HTTP validation, DTO -> command mapping, use-case invocation, and HTTP response mapping. Do not load business entities or call business repositories from the controller seam.
- `application`: keep HTTP mappers limited to transport-to-use-case translation. Do not compute business state transitions, updated lines, pricing totals, or token balances in a controller helper or mapper.
- `domain`: keep a use-case-to-port seam inside the test code. Do not create concrete infrastructure implementations.
- `domain`: keep business orchestration, repository usage through ports, authorization or ownership rules, workflow, and domain services inside the use case or domain seam. Do not introduce HTTP concepts, API DTOs, `ResponseEntity`, or status codes.
- `infrastructure`: keep the implementation aligned with the existing domain port contract. Do not move business validation rules into the repository.
  - Shape the temporary seam like a future concrete JPA repository or adapter when the selected scenarios are about persistence, retrieval, update, or query behavior.
  - Keep domain and persistence concerns separate in the temporary test code when distinct JPA persistence entities or technical models will be needed later.
  - If no matching domain port or interface exists yet for the targeted repository behavior, record that dependency explicitly in the Green notes so Refactor can extract the minimum pure-domain port before promoting the adapter.
  - Record explicitly in the Green notes when Refactor must create separate JPA entities, a domain-to-persistence mapper, and the minimum JPA wiring.

## Hard stops
- Do not refactor during Green.
- Do not update another layer.
- Do not ignore the `targetTestClass` selected by the Red step to create a parallel test class when that file still exists.
- Do not reinterpret the feature family when `featureFolder` and `resolvedIssueFiles` already resolved it during Red.
- Do not widen the implementation to all scenarios in the class if only a subset was requested.
- Do not create production classes under `src/main`.
- Do not create a parallel top-level concept when an equivalent one already exists.
- Do not make an application API or endpoint scenario green by directly invoking a plain Java method that has no real route or handler semantics.
- Do not hide a missing domain repository contract behind a temporary infrastructure seam without reporting whether Refactor must extract a minimal pure-domain port.
- Do not let the temporary infrastructure seam imply JDBC or another persistence technology when the slice will be promoted as a real persistence implementation during Refactor; the target technology is JPA.
- Do not return `GREEN` if at least one selected scenario test is not `PASSED`.
- Do not set handoff fields to non-null values when `status` is `PARTIAL` or `BLOCKED`.
- Do not include methods, variables, or production symbols in `implemented_code`; include only class, enum, or interface names implemented inside the test class.

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
  "notes": ["string"],
  "test_file_path": "string | null",
  "test_method_name": "string | null",
  "implemented_code": ["string"]
}
```

### Handoff semantics
- `test_file_path`: path to the test file that should be used as the starting point for Refactor.
- `test_method_name`: one selected scenario test method that is now green and can drive Refactor first.
- `implemented_code`: list of class, enum, or interface symbols implemented in the test class to make selected tests pass.
- If several selected methods passed, choose one primary method for `test_method_name` and keep the others in `selectedTestMethods`.
- If `status` is `PARTIAL` or `BLOCKED`, return `test_file_path = null`, `test_method_name = null`, and `implemented_code = []`.

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
  ],
  "test_file_path": "application/src/test/java/com/example/application/order/PlaceDrinkOrderControllerTest.java",
  "test_method_name": "shouldReturnUnauthorizedWhenSubmittingAvailableDrinkOrderWithoutFestivalGoerIdentifierScenario5",
  "implemented_code": [
    "RecordingPlaceDrinkOrderUseCase"
  ]
}
```