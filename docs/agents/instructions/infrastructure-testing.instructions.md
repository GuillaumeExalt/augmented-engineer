# Infrastructure Testing Guidelines

- Focus on repository/persistence behavior and external adapters.
- Use in-memory databases or testcontainers for integration tests when needed.
- Validate data mapping between entities and persistence models.
- Ensure transactional behavior is tested when relevant.
- Reserve a `ScenarioN` suffix for tests that map 1:1 to an existing numbered scenario in `docs/features/...`.
- For extra infrastructure tests outside the documented acceptance scenarios, do not invent a new scenario number; use a descriptive name with `TechnicalCase` or `RegressionCase` if needed.
- Keep tests isolated and clean up test data between runs.
