# Application Testing Guidelines

- Use integration-style tests for controllers and request handling.
- Prefer Spring Boot Test slice for controllers or use lightweight web test frameworks.
- Validate HTTP status codes, request/response body shapes, and input validation errors.
- Test names should reflect endpoint and expected outcome: `shouldReturn404WhenUserNotFound`.
- Keep external integrations mocked or use test doubles.
