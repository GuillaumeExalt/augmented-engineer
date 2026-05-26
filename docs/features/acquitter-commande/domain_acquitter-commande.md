# Acquitter une commande et fournir un temps estime - Domaine

<!-- github-issue: GuillaumeExalt/augmented-engineer#2 -->

**Contexte**
Le domaine doit permettre au barman d'acquitter une commande, de calculer le temps estime de preparation selon les regles metier, puis de basculer la commande en preparation.

**Critères d'acceptation**

Feature: Domaine - Acquitter une commande avec calcul ETA

Scenario: 1 - Calculer le temps pour des boissons non alcoolisees uniquement
Given une commande contenant deux types de boissons non alcoolisees
When le barman acquitte la commande
Then la commande passe a l'etat en preparation
And le temps estime est de 2 minutes

Scenario: 2 - Calculer le temps pour une commande avec repas et boisson
Given une commande contenant un type de repas et une boisson alcool premium
When le barman acquitte la commande
Then la commande passe a l'etat en preparation
And le temps estime est de 13 minutes

Scenario: 3 - Additionner correctement les temps pour une commande mixte sans repas
Given une commande contenant une boisson non alcoolisee, une boisson alcool normale et une boisson alcool premium
When le barman acquitte la commande
Then la commande passe a l'etat en preparation
And le temps estime est la somme des temps de chaque type de boisson

**Notes**
- Boisson non alcoolisee: 1 minute par type de boisson.
- Boisson alcool normale: 2 minutes par boisson.
- Boisson alcool premium: 3 minutes par boisson.
- Repas: 10 minutes par type de repas plus le temps boisson le plus long.
- Snack: 2 minutes par type de snack.
