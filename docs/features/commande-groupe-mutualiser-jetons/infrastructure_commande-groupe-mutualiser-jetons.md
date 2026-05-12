# Mutualiser des jetons pour une commande de groupe - Infrastructure

**Contexte**
L'infrastructure doit persister une commande de groupe et les debits de jetons de plusieurs festivaliers dans un meme flux transactionnel afin d'eviter les etats partiels.

**Critères d'acceptation**

Feature: Infrastructure - Persister une commande de groupe

Scenario: 1 - Persister une commande et les contributions de tous les festivaliers
Given une commande de groupe validee en domaine
When l'infrastructure enregistre la commande et tous les mouvements de jetons associes
Then la commande est stockee avec succes
And chaque contribution est tracee avec son festivalier source

Scenario: 2 - Annuler toute persistance si un debit echoue
Given le debit de jetons d'un festivalier echoue pendant la transaction
When l'infrastructure cloture l'operation
Then aucune contribution n'est persistée
And aucune commande de groupe partielle n'est conservee

Scenario: 3 - Consigner l'historique des contributions
Given une commande de groupe a ete enregistree avec succes
When l'infrastructure ecrit l'historique fonctionnel
Then chaque ligne indique le festivalier, le type de jeton et le montant deduit
And les donnees sont exploitables pour audit

**Notes**
- Les debits multi-festivaliers doivent etre atomiques.
- Le modele de persistance doit permettre la tracabilite fine des contributions.
