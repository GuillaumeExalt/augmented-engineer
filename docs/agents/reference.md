# Reference — Assistant Software Engineer Agent

## Output format

Les issues sont des fichiers Markdown autonomes avec les sections suivantes :

| Section | Required | Description |
|---|---|---|
| `# Title` | ✅ | Titre concis de l'issue. Inclure le nom du module impacté lorsque la fonctionnalité s'étend sur plusieurs modules. |
| `**Contexte**` | ✅ | Contexte fonctionnel expliquant *pourquoi* l'issue existe et *qui* elle concerne. |
| `**Critères d'acceptation**` | ✅ | Un bloc Gherkin `Feature:` avec 1–N entrées `Scenario:` couvrant le chemin heureux et les cas limites. |
| `**Notes**` | ❌ | Optionnel. Toutes références supplémentaires, contraintes, ou questions ouvertes. |

## Conventions de nommage et architecture

Suivre l'Architecture Hexagonale comme décrit dans AGENTS.md.

- **Ports** : Les interfaces se terminent par "Port", e.g., `OrderRepositoryPort`
- **Adaptateurs** : Les classes se terminent par "Adapter", e.g., `JpaOrderRepositoryAdapter`
- **Cas d'usage** : Les classes se terminent par "UseCase", e.g., `PlaceOrderUseCase`
- **Entités** : Classes dans le domaine, e.g., `Order`, `FestivalGoer`
- **DTOs** : Dans l'application, e.g., `OrderRequestDto`
- **Contrôleurs** : Dans l'application, e.g., `OrderController`

## Règles métier spécifiques

- Les tokens ne peuvent pas être négatifs.
- Les tokens non dépensés ne sont pas reportés au jour suivant.
- Les commandes de groupe nécessitent une contribution suffisante de tokens.

## File naming convention

Créez les fichiers d'issue dans le dossier docs/features/, en utilisant des noms descriptifs comme `consult-balance.md`, `place-order.md`, etc.