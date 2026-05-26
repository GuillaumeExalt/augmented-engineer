---
name: create-issue
description: Generate structured development issues in local markdown files with title, context, acceptance criteria, and Gherkin scenarios from a functional request.
---

# Instructions

1. Extract the functional request context and the expected success criteria from the user input.
2. Ask 2-3 clarification questions if the request is ambiguous, incomplete, or too broad.
3. Identify the impacted modules in the repository: domain, application, infrastructure.
4. If more than one module is impacted, generate one issue file per module.
5. For each module:
    1. Summarize the module-specific context.
    2. Identify the module-specific acceptance criteria.
    3. Generate a concise, explicit title.
    4. Produce 1..N Gherkin scenarios covering the happy path and relevant edge cases.
    5. Create the issue file under `docs/features/{feature_name}/{module_name}_{issue_title}.md`.
    6. If an issue file already exists, preserve any existing `<!-- github-issue: owner/repo#123 -->` sync marker comment.
    7. Each features/{feature_name} directory should contain a domain_{feature_name}.md file that models the domain concepts and rules related to the feature.
    8. Each features/{feature_name} directory should contain a application_{feature_name}.md file that models the domain concepts and rules related to the feature.
    9. Each features/{feature_name} directory should contain a infrastructure_{feature_name}.md file that models the domain concepts and rules related to the feature.
    10. The Text should be only in french
    11. Use the `templates/issue.md` template format for the file.
    12. Validate the created issue using `python scripts/validate_issue_format.py <issue_file>`.

# Note

- This skill creates local Markdown issue files only. It does not publish GitHub issues.
- To publish or sync those files to a mirror repository via MCP, use the `GitHub Issue Sync` agent or prompt.
- This skill is intended to create manageable, testable issues. Do not create a single issue that tries to cover multiple unrelated modules.
- If the request is too broad or crosses many concerns, ask the user to break it down by module or feature.
- Prefer one issue per impacted module when the feature spans domain, application, and infrastructure.
- Do not include implementation code in the issue files; keep them focused on context, acceptance criteria, and behavior.
