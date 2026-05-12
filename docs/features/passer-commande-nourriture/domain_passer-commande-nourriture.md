# Passer une commande de nourriture - Domaine

**Contexte**
Le domaine doit appliquer les regles de cout des articles alimentaires et valider qu'un festivalier dispose de suffisamment de jetons nourriture avant d'accepter la commande.

**Critères d'acceptation**

Feature: Domaine - Commander de la nourriture

Scenario: 1 - Commander un snack avec solde suffisant
Given un festivalier avec 2 jetons nourriture
When il commande un snack
Then la commande est acceptee
And le solde de jetons nourriture devient 1

Scenario: 2 - Commander un repas avec solde suffisant
Given un festivalier avec 4 jetons nourriture
When il commande un repas
Then la commande est acceptee
And le solde de jetons nourriture devient 1

Scenario: 3 - Rejeter une commande si les jetons nourriture sont insuffisants
Given un festivalier avec 2 jetons nourriture
When il commande un repas
Then la commande est rejetee
And le solde de jetons nourriture reste a 2

**Notes**
- Un snack coute 1 jeton nourriture.
- Un repas coute 3 jetons nourriture.
