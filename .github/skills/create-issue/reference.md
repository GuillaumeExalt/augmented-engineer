# Reference — create-issue

## Output format

Each issue is a standalone Markdown file with the following sections:

| Section | Required | Description |
|---|---|---|
| `# Title` | ✅ | Concise title of the issue. Include the impacted module name when the feature spans multiple modules. |
| `**Contexte**` | ✅ | Functional context explaining *why* the issue exists and *who* it concerns. |
| `**Critères d'acceptation**` | ✅ | A Gherkin `Feature:` block with 1–N `Scenario:` entries covering the happy path and edge cases. |
| `**Notes**` | ❌ | Optional. Any additional references, constraints, or open questions. |

## File naming convention
Créez ce fichier dans votre dossier .github/skills/create-issue/, et vérifiez que l'agent l'exploite correctement lorsqu'il génère une issue avec plusieurs modules.

## Publication GitHub

Ce skill génère uniquement les fichiers d'issue locaux sous `docs/features/...`.
Pour publier ou synchroniser ces issues vers un repository miroir GitHub via MCP, utilisez l'agent ou le prompt `GitHub Issue Sync`.