# Domain Testing Guidelines

- Use JUnit 5 for unit tests.
- Keep tests pure: do not touch external resources.
- Use fixtures or test state helpers located under `domain/src/test/java/.../fixture`.
- Test names should be descriptive: `should<ExpectedBehavior>When<Condition>`.
- Reserve a `ScenarioN` suffix for tests that map 1:1 to an existing numbered scenario in `docs/features/...`.
- For extra domain tests outside the documented acceptance scenarios, do not invent a new scenario number; use a descriptive name with `TechnicalCase` or `RegressionCase` if needed.
- Arrange/Act/Assert structure is recommended.
- Tests must be deterministic and fast.
