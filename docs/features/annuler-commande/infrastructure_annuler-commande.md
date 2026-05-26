# Annuler une commande - Infrastructure

<!-- github-issue: GuillaumeExalt/augmented-engineer#6 -->

**Contexte**
L'infrastructure doit persister l'annulation de commande et le remboursement des jetons dans un traitement atomique, puis emettre la notification de confirmation au festivalier.

**Critères d'acceptation**

Feature: Infrastructure - Annulation et remboursement des jetons

Scenario: 1 - Persister annulation et remboursement dans la meme transaction
Given une annulation validee par le domaine
When l'infrastructure met a jour la commande et enregistre les mouvements de remboursement
Then la commande est persistée avec l'etat annulee
And les credits de jetons sont persistés dans la meme transaction

Scenario: 2 - Envoyer une notification de confirmation d'annulation
Given une annulation a ete persistee avec succes
When l'infrastructure publie les evenements associes
Then une notification de confirmation est envoyee au festivalier
And la notification contient les montants rembourses

Scenario: 3 - Eviter les etats partiels en cas d'erreur
Given une erreur technique survient pendant la transaction d'annulation
When l'infrastructure termine le traitement
Then ni l'etat annule ni les credits de jetons ne sont persistés partiellement
And l'application recoit une erreur technique exploitable

**Notes**
- Le remboursement doit etre strictement coherent avec les jetons initialement debites.
- Les notifications doivent etre journalisees pour audit.
