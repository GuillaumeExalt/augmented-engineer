# Reference — Assistant Software Engineer Agent

## Output format

Les issues sont des fichiers Markdown autonomes avec les sections suivantes :

| Section | Required | Description |
|---|---|---|
| `# Title` | ✅ | Titre concis de l'issue. Inclure le nom du module impacté lorsque la fonctionnalité s'étend sur plusieurs modules. |
| `**Contexte**` | ✅ | Contexte fonctionnel expliquant *pourquoi* l'issue existe et *qui* elle concerne. |
| `**Critères d'acceptation**` | ✅ | Un bloc Gherkin `Feature:` avec 1–N entrées `Scenario:` couvrant le chemin heureux et les cas limites. |
| `**Notes**` | ❌ | Optionnel. Toutes références supplémentaires, contraintes, ou questions ouvertes. |

## Alignement entre issues et tests

- Les `Scenario:` documentes dans `docs/features/...` sont la source de verite pour les tests de scenario.
- Sauf demande explicite de mise a jour fonctionnelle, il est interdit de modifier, reordonner, renommer, fusionner, scinder ou reecrire les `Scenario:` documentes dans `docs/features/...`.
- Un test ne peut utiliser un suffixe `ScenarioN` que si ce numero existe deja dans l'issue ou la feature correspondante.
- Les tests supplementaires utiles mais hors scenario d'acceptation ne doivent jamais etre numerotes comme de faux scenarios.
- Pour ces tests hors scenario, utilisez un nom descriptif classique, avec `TechnicalCase` ou `RegressionCase` si un suffixe explicite est utile.
- Si un nouveau scenario d'acceptation est necessaire, il faut demander une mise a jour explicite de la documentation fonctionnelle avant de creer un test `ScenarioN`.

## Conventions de nommage et architecture

Suivre l'Architecture Hexagonale comme décrit dans AGENTS.md.

- **Ports** : Les interfaces se terminent par "Port", e.g., `OrderRepositoryPort`
- **Ports sortants** : Toute lecture/ecriture de base de donnees, publication d'evenement, appel HTTP, ou acces a un systeme externe requis par un use case domaine doit passer par une interface definie dans le domaine (`domain/.../port/out/...`).
- **Use cases domaine** : Un use case ne doit pas se contenter de retourner un objet en esperant qu'une couche suivante persiste plus tard l'etat metier si la persistance fait partie du comportement attendu; il doit appeler le ou les ports necessaires.
- **Adaptateurs** : Les classes se terminent par "Adapter", e.g., `JpaOrderRepositoryAdapter`
- **Infrastructure** : L'infrastructure implemente les ports du domaine et porte les details techniques (JPA, SQL, mapping, transaction, clients externes). Elle ne redefinit pas le contrat metier.
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
