Notes on prompt iteration for TDD Red step

What I corrected/improved:
- Added explicit routing guidance: prefer `domain` when ambiguous and ask a clarifying question.
- Added CRITICAL rules forbidding production code creation in this step and requiring test doubles to live under `tests/`.
- Added naming conventions for test files and methods with concrete examples.
- Added negative examples to prevent merging modules or adding production code.

Why these changes:
- During the exercise, the agent sometimes chose layers ambiguously and risked writing production code.
- The explicit CRITICAL rules prevent accidental implementation of production logic during the Red step.

Next actions:
- Use `/clear` in the chat interface and re-run the prompt with the same scenario.
- Iterate on the prompt if the agent still produces undesired files.
