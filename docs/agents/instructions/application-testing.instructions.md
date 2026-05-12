# Application Testing Guidelines

- Use integration-style tests for controllers and request handling.
- Prefer Spring Boot Test slice for controllers or use lightweight web test frameworks.
- Validate HTTP status codes, request/response body shapes, and input validation errors.
- Test names should reflect endpoint and expected outcome: `shouldReturn404WhenUserNotFound`.
- Reserve a `ScenarioN` suffix for tests that map 1:1 to an existing numbered scenario in `docs/features/...`.
- For extra application tests outside the documented acceptance scenarios, do not invent a new scenario number; use a descriptive name with `TechnicalCase` or `RegressionCase` if needed.
- Keep external integrations mocked or use test doubles.
