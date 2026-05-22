---
agent: agent
name: TDD Refactor step
description: "Use when you want to refactor only one layer and only the selected scenario numbers from a layer-scoped Green result: move production-worthy code out of tests for that slice, keep behavior unchanged, keep the targeted tests green after each micro-step, isolate non-trivial mapping code in dedicated mapper packages, promote application API slices to real framework-backed controllers with route metadata and minimum web wiring, and for infrastructure persistence slices extract the minimum pure-domain repository contract when needed before promoting a real JPA-backed repository with separate persistence entities, mapper, and JPA configuration."
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
- move non-trivial inter-layer mapping into dedicated mapper code instead of leaving it inline in controllers or repositories when distinct models are involved
- for application API slices, finish on a real framework-backed HTTP controller or handler with explicit route metadata instead of a plain Java object called directly by tests
- for infrastructure persistence slices, prefer a concrete repository or adapter implementation over extracting only passive data carriers
- for infrastructure persistence slices that describe persisted state, finish on a real JPA-backed implementation with separate persistence types, mapper code, and the minimum required JPA wiring instead of an in-memory substitute

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
- If `requestedLayer == infrastructure` and the selected scenarios describe repository or persistence behavior, the refactor may create the minimum missing pure-domain repository port or query contract required by the selected scenarios before promoting the concrete infrastructure adapter.
- If that missing prerequisite would exceed the selected scenario slice, require business behavior changes outside the contract, or force application production changes, return `BLOCKED`.

## Single-layer refactor rule
- Refactor only the `requestedLayer` as the primary ownership layer from the Green result.
- Do not create production code in another layer during this prompt, except the minimum pure-domain repository port or related domain-side contract types strictly required by an infrastructure repository slice.
- If the requested layer needs broader cross-layer changes than that minimal prerequisite contract, stop and return `BLOCKED`.

## Layer routing rules
- `application`:
  - move transport entrypoints under `controller`
  - when the selected scenarios are API-shaped, implement a real framework-backed controller or handler with explicit HTTP method and path declaration for the documented route
  - move request DTOs under `model/in`
  - move response DTOs under `model/out`
  - move request-to-domain and domain-to-response translation under `mapper` when DTOs and domain models are distinct or the mapping is more than trivial field passthrough
  - before promoting code, verify no business logic or repository access lives in the controller; if found, move it toward the use case or domain instead of preserving it in transport code
  - keep the controller focused on transport concerns and delegation, not non-trivial object mapping logic
  - controllers may only read HTTP inputs, perform basic HTTP validation, map DTO -> command, call a use case, and map result or exception to HTTP response
  - controllers must not load business entities, call business repositories, contain business rules, ownership or authorization logic, workflow logic, aggregate mutation, or business decisions
  - add the minimum web framework and test wiring required by the selected scenarios when no real HTTP surface exists yet and that wiring stays within the slice
  - do not create domain or infrastructure production code here
- `domain`:
  - move business logic under `usecase`
  - move domain models, commands, results, enums, and typed business exceptions under `model`
  - move repository interfaces under `port/out`
  - when supporting an infrastructure repository refactor, add only the pure domain repository port and domain-side query/result/value types strictly required by the selected scenarios
  - use cases must orchestrate business behavior, load entities via ports or repositories, enforce business rules, ownership and permissions, execute workflow, call domain services, and return explicit business results or exceptions
  - use cases must not know HTTP, Spring MVC, `ResponseEntity`, API DTOs, or HTTP status codes
  - do not create concrete infrastructure implementations here
- `infrastructure`:
  - move concrete adapters and repositories under `repository`
  - implement the existing repository or adapter interface defined in the domain when the selected scenarios are about persistence, retrieval, update, or query behavior
  - if that repository port does not exist yet, extract the minimum pure-domain contract first and then implement it here
  - move technical inputs under `model/in`
  - move technical outputs under `model/out`
  - move persistence entities or records under `model/entity` or `model/persistence`
  - move domain-to-persistence and persistence-to-domain mapping code under `mapper`
  - for persistence slices, do not keep production repositories backed only by in-memory collections once the refactor is declared complete
  - keep JPA annotations and configuration inside infrastructure only
  - keep domain entities free of ORM annotations and infrastructure dependencies
  - do not create application or domain business code here

## Application API slice rule
- When the selected application scenarios express HTTP or API behavior, the default refactor target is a concrete framework-backed controller or handler under `src/main`, not a plain Java object invoked directly by tests.
- For those scenarios, `REFACTORED` means explicit route declaration or handler metadata for the documented HTTP method and path, plus real request/response binding for the selected slice.
- If the project currently lacks a web framework for the selected application slice, add only the minimum web framework and controller-test wiring needed for the selected scenarios; otherwise return `BLOCKED` rather than shipping a fake controller.
- Keep transport concerns in the controller and move non-trivial DTO-to-domain or domain-to-response mapping into a dedicated mapper when needed.
- Keep business authorization and ownership rules in the use case or domain, never in the controller.
- Do not declare `REFACTORED` for an application API slice if tests still prove behavior only by calling a plain Java method directly.

## Infrastructure persistence slice rule
- When the selected infrastructure scenarios express repository behavior, the default refactor target is a concrete repository or adapter class under `repository`, not only persistence records.
- That concrete repository or adapter **MUST** implement a domain port or interface. If the port does not exist yet, extract the minimum pure-domain port and any domain-side query/result types required by the selected scenarios before implementing the adapter.
- When the selected scenarios assert persisted behavior, `REFACTORED` means a real JPA-backed implementation under `src/main`, not an in-memory repository promoted from the test.
- Use separate persistence entities or records under `model/entity` or `model/persistence` for the production repository. Do not use domain entities as the infrastructure storage representation in production code.
- Add mapping code under `mapper` for domain-to-persistence and persistence-to-domain translation. Do not leave production persistence translation inline inside the repository when distinct models exist.
- Add the minimum real JPA configuration needed for the selected slice inside infrastructure only, such as entity annotations, relationship mapping, persistence setup, and repository wiring.
- The only allowed cross-layer prerequisite for this slice is the pure domain contract described above. Do not move JPA annotations, persistence entities, mapper code, or framework dependencies into the domain.
- If the Green slice only introduced in-test records or an in-memory repository seam, treat those as temporary scaffolding. Promote them to a real JPA-backed repository adapter only when the domain contract already exists or has been extracted first in the same refactor step.
- If the project currently lacks JPA support for the selected slice, add only the minimum JPA wiring that stays inside the chosen scenarios; otherwise return `BLOCKED` rather than shipping an in-memory substitute in `src/main`.
- If the selected scenarios are repository-shaped and the only promotable symbols are passive records, that is not sufficient for `REFACTORED`; continue to the real adapter or repository and its domain contract, or return `BLOCKED`.
- Prefer this promotion order when applicable: domain port/query contract -> persistence model -> mapper -> repository or adapter implementation -> JPA wiring -> query-specific details.

## Instructions
1. Parse the provided Green JSON.
2. Confirm that it is a single-layer Green result.
3. Read the selected target test file and only the symbols introduced for that slice.
4. For `application`, also resolve the closest matching HTTP or web wiring surface already present in the module before deciding what is production-worthy.
  - If the selected scenarios assert HTTP method, route, status, headers, or body behavior, determine whether the minimum real web framework setup needed for those scenarios stays within the slice. If not, return `BLOCKED`.
5. For `infrastructure`, also resolve the closest matching domain port or interface, the nearest existing infrastructure wiring surface, and the currently available JPA surface before deciding what is production-worthy.
  - If the port is missing, define the smallest pure-domain repository contract that satisfies only the selected scenarios before designing the adapter.
  - If the selected scenarios assert persisted behavior, determine whether the minimum real JPA setup needed for those scenarios stays within the slice. If not, return `BLOCKED`.
6. Decide which symbols are production-worthy and which symbols must remain test-only.
  - For application slices, first decide whether DTO-to-domain or domain-to-DTO translation deserves a dedicated mapper for the selected scenarios.
  - For application API slices, first decide what the concrete controller or handler contract is, then what route declaration, binding, and minimum web configuration are required to support it.
  - For infrastructure persistence slices, first decide what the repository or adapter contract is, then what persistence model, mapping, and real JPA configuration are required to support it.
  - If the selected slice needs a domain repository interface that does not exist yet, create that minimal pure-domain contract first.
  - Return `BLOCKED` only when that prerequisite grows beyond the selected scenarios or requires business behavior changes outside the repository contract.
7. Perform one micro-step at a time:
   - create or update one production symbol
   - switch the test to use it
   - run the narrowest affected validation
   - remove obsolete test-only duplication if safe
   - rerun the same validation
  - For application API slices, prefer this promotion order when applicable: request or response DTO -> mapper -> controller or handler with explicit route metadata -> minimal web wiring.
  - For infrastructure persistence slices, prefer this promotion order when applicable: domain port/query contract -> persistence model -> mapper -> repository or adapter implementation -> JPA wiring -> query-specific details.
8. Finish by running the full targeted class for that layer.
9. If `requestedLayer == infrastructure` and production code under `src/main` changed, also run validations that prove the shared contract still holds for `domain`, `infrastructure`, and `application`.
  - When a new domain port or shared contract type was created or changed, execute at least one real validation in each module when available; otherwise fall back to the full module test suites.
  - When the selected infrastructure slice asserts persisted behavior, execute at least one real infrastructure validation that exercises the JPA mapping and repository code used by the promoted repository.
10. Return only valid JSON.

## Requirements
- You **MUST** keep the refactor scoped to the Green layer and scenario slice.
- You **MUST** preserve the selected test method names exactly.
- You **MUST** keep behavior unchanged.
- You **MUST** move only production-worthy code out of tests.
- You **MUST** keep test-only doubles, fixtures, and scaffolding in test sources.
- You **MUST** validate after every micro-step when a relevant test exists.
- You **MUST** extract dedicated mapper code in the owning layer when the selected slice translates between distinct application, domain, or persistence models with non-trivial mapping.
- You **MUST** promote application API slices to a real framework-backed controller or handler with explicit route metadata for the documented HTTP method and path.
- You **MUST** add the minimum web framework and test wiring required by the selected application scenarios when no real HTTP surface exists yet and that work stays within the slice.
- You **MUST** keep the application flow aligned with `HTTP Controller -> HTTP Mapper -> Application Use Case -> Domain -> Repository Port -> Infrastructure`.
- You **MUST** keep business authorization, ownership, workflow, aggregate mutation, and repository usage out of controllers.
- You **MUST** extract the minimum pure-domain repository port or interface first when an infrastructure repository slice needs it, and you **MUST** return `BLOCKED` if that prerequisite would exceed the selected scenario slice.
- You **MUST** validate affected `domain`, `infrastructure`, and `application` tests when an infrastructure repository slice creates or changes shared production code.
- You **MUST** choose JPA as the persistence technology for infrastructure persistence slices promoted during this prompt.
- You **MUST** keep JPA annotations, persistence configuration, and infrastructure dependencies out of the domain layer.
- You **MUST** use separate persistence types in infrastructure instead of annotating or leaking domain entities when a production persistence repository is promoted.
- You **MUST** create a mapper between domain objects and persistence objects for infrastructure persistence slices that move beyond test scaffolding.
- You **MUST** add the minimum real JPA configuration required by the selected infrastructure scenarios when those scenarios assert persisted behavior.
- You **MUST NOT** declare `REFACTORED` for a persistence slice if the production repository remains only an in-memory collection-backed implementation.
- You **MUST NOT** add new scenarios.
- You **MUST NOT** widen the refactor to another layer.
- You **MUST NOT** create cross-layer production code just because it would be convenient.

## Hard stops
- Do not refactor the whole feature if Green only targeted one scenario slice.
- Do not create domain production classes from an application refactor slice.
- Do not create infrastructure implementations from a domain refactor slice.
- Do not move business validation rules into controllers.
- Do not move technical persistence concerns into the domain.
- Do not declare `REFACTORED` for an application API slice if the production controller remains a plain Java object called directly by tests.
- Do not ship an application API slice without a real route or handler declaration for the documented method and path when the issue explicitly asserts HTTP behavior.
- Do not extract a broad domain repository API when the selected infrastructure slice only needs a narrow contract for the chosen scenarios.
- Do not declare `REFACTORED` after moving only passive records or data carriers if repository behavior remains test-only.
- Do not declare `REFACTORED` for a persistence slice backed only by an in-memory repository under `src/main`.
- Do not choose JDBC or another persistence technology for a repository slice that this prompt promotes to real persistence; the mandated target is JPA.
- Do not add JPA annotations or persistence-framework dependencies to domain entities.
- Do not use a domain entity or domain record directly as the persistence entity of a production infrastructure repository just to avoid creating `model/entity` or `model/persistence` types.
- Do not keep non-trivial application-to-domain or domain-to-persistence mapping buried inline in controllers or repositories when the selected slice uses distinct models.
- Do not bundle domain port extraction, JPA wiring, and query-specific repository behavior into one unvalidated edit.
- Do not collapse domain entities and persistence entities into one type just to save time.
- Do not change assertions or expected values in the selected tests.
- Do not stop at a compile-only check. Run the tests.

## Infrastructure review checklist
- Verify the concrete repository or adapter implements the domain port extracted or reused for the selected slice.
- Verify the production repository is JPA-backed for persistence scenarios and is not only an in-memory collection wrapper.
- Verify persistence entities, mappers, JPA annotations, and configuration stay in `infrastructure` only.
- Verify a dedicated mapper exists between domain objects and persistence objects when the production repository uses distinct persistence types.
- Verify no infrastructure annotation, framework type, or dependency leaks into domain entities or domain repository contracts.
- Verify the selected infrastructure tests still pass and that real `domain`, `infrastructure`, and `application` validations executed successfully.
- Verify at least one real infrastructure validation exercised the promoted JPA mapping, persistence setup, and repository behavior for the selected slice.

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