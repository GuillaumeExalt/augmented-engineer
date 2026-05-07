# Domain Testing Guidelines

- Use JUnit 5 for unit tests.
- Keep tests pure: do not touch external resources.
- Use fixtures or test state helpers located under `domain/src/test/java/.../fixture`.
- Test names should be descriptive: `should<ExpectedBehavior>When<Condition>`.
- Arrange/Act/Assert structure is recommended.
- Tests must be deterministic and fast.
