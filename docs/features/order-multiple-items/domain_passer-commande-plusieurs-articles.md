# Passer une commande de plusieurs articles - Domaine

**Contexte**
Le domaine doit accepter une commande composee de plusieurs articles tout en verifiant que le cout total ne depasse pas le solde de jetons boisson ni le solde de jetons nourriture du festivalier.

**Critères d'acceptation**

Feature: Domaine - Commander plusieurs articles

Scenario: 1 - Accepter une commande mixte si les deux soldes sont suffisants
Given un festivalier avec 3 jetons boisson et 4 jetons nourriture
When il commande une boisson alcool normale et un repas
Then la commande est acceptee
And le solde de jetons boisson devient 2
And le solde de jetons nourriture devient 1

Scenario: 2 - Rejeter la commande si les jetons boisson sont insuffisants
Given un festivalier avec 0 jeton boisson et 5 jetons nourriture
When il commande une boisson alcool normale et un snack
Then la commande est rejetee
And les soldes de jetons restent inchanges

Scenario: 3 - Rejeter la commande si les jetons nourriture sont insuffisants
Given un festivalier avec 4 jetons boisson et 0 jeton nourriture
When il commande une boisson non alcoolisee et un repas
Then la commande est rejetee
And les soldes de jetons restent inchanges

**Notes**
- Le cout total est evalue par type de jetons.
- Une boisson non alcoolisee ne consomme pas de jeton boisson.
