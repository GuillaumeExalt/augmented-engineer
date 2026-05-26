# Modifier une commande - Infrastructure

<!-- github-issue: GuillaumeExalt/augmented-engineer#9 -->

**Contexte**
L'infrastructure doit persister de facon coherente soit la mise a jour directe de la commande non acquittee, soit la creation d'une demande de changement associee a une commande deja acquittee.

**Critères d'acceptation**

Feature: Infrastructure - Persistance des modifications de commande

Scenario: 1 - Persister la mise a jour d'une commande non acquittee
Given une commande non acquittee validee pour modification
When l'infrastructure enregistre les nouvelles lignes de commande et les ajustements de jetons
Then les changements sont persistés dans une transaction unique
And l'etat final de la commande est coherent

Scenario: 2 - Persister une demande de changement pour commande acquittee
Given une commande acquittee et une demande de changement valide
When l'infrastructure enregistre la demande
Then la demande de changement est persistée avec un statut en attente
And un message est publie pour notifier le barman

Scenario: 3 - Eviter les mises a jour partielles en cas d'erreur
Given une erreur technique survient pendant l'enregistrement
When l'infrastructure cloture la transaction
Then aucun changement partiel n'est conserve
And l'application recoit une erreur technique exploitable

**Notes**
- Les ecritures de commande, de jetons et de demande de changement doivent etre atomiques.
- Les notifications barman doivent etre tracables.
