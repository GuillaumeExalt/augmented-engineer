# Application Testing Guidelines

- Use integration-style tests for controllers and request handling.
- Prefer Spring Boot Test slice for controllers or use lightweight web test frameworks.
- For API scenarios that mention HTTP verbs, paths, status codes, headers, or response bodies, drive tests through a real HTTP-facing test harness rather than direct invocation of a plain Java method.
- Validate route declaration and HTTP method binding when the scenario documents a concrete endpoint such as `POST /commandes`.
- Validate HTTP status codes, request/response body shapes, and input validation errors.
- Test names should reflect endpoint and expected outcome: `shouldReturn404WhenUserNotFound`.
- Never edit, reorder, rename, split, merge, or rewrite the documented `Scenario:` entries in `docs/features/...` unless the user explicitly asks for a functional documentation change.
- Reserve a `ScenarioN` suffix for tests that map 1:1 to an existing numbered scenario in `docs/features/...`.
- For extra application tests outside the documented acceptance scenarios, do not invent a new scenario number; use a descriptive name with `TechnicalCase` or `RegressionCase` if needed.
- Keep external integrations mocked or use test doubles.
