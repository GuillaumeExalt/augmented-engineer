# Infrastructure Testing Guidelines

- Focus on repository/persistence behavior and external adapters.
- Use in-memory databases or testcontainers for integration tests when needed.
- Validate data mapping between entities and persistence models.
- Ensure transactional behavior is tested when relevant.
- Keep tests isolated and clean up test data between runs.
