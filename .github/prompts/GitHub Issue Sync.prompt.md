---
name: GitHub Issue Sync
description: "Publish or sync docs/features markdown issues to a GitHub mirror repository via MCP."
argument-hint: "repo=owner/repo mode={publish-existing|generate-and-publish}\nissue=docs/features/.../*.md | feature=docs/features/... | Request: ..."
agent: GitHub Issue Sync
tools: [read, search, edit, execute, web, todo, github/*]
model: GPT-5.4 (copilot)
---

Synchronize local markdown issues under `docs/features/...` with GitHub issues in the mirror repository.

Accepted inputs:
- `repo=owner/repo feature=docs/features/<feature-name> mode=publish-existing`
- `repo=owner/repo issue=docs/features/<feature-name>/<layer>_<feature-name>.md mode=publish-existing`
- `repo=owner/repo mode=generate-and-publish` followed by the functional request to split into issues

Rules:
- Treat local markdown issue files as the source of truth.
- Preserve or add the sync marker `<!-- github-issue: owner/repo#123 -->` after successful publication.
- Use real GitHub MCP issue creation/update tools; do not report success without an actual remote operation.
- Prefer updating an existing mapped GitHub issue over creating duplicates.
- Use a minimal GitHub issue payload by default: `title` and `body`, and only add optional fields when they have real values.
- Never invent a milestone id and never send `milestone: 0`; if a tool forces that shape, switch to another issue write tool instead of retrying the same invalid call.
- On `422 Validation Failed`, retry at most once with a leaner compatible GitHub issue write tool or payload.
- If the mapped remote issue already matches the local title and body, skip the update instead of sending a no-op write call.
- Never call a GitHub issue create/update tool that requires placeholder `assignees`, `labels`, or `milestone` values just to satisfy its schema.
- If the only exposed issue-write tool would force placeholder values such as empty arrays or `milestone: 0`, return `BLOCKED` instead of triggering a predictable GitHub validation failure.
- Prefer a session-exposed issue-write capability that accepts a true minimal payload over a repository tool whose local wrapper forces invalid placeholder fields.

Valid create payload example:

```json
{
	"title": "Annuler une commande - Application",
	"body": "..."
}
```

Invalid create payload example:

```json
{
	"title": "Annuler une commande - Application",
	"body": "...",
	"assignees": [],
	"labels": [],
	"milestone": 0
}
```

For create calls, omission means "leave unset". Never synthesize empty arrays or `0` as placeholders.
For update calls, send `assignees: []` or `labels: []` only when you intentionally want to clear those fields on GitHub.