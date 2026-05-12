# Passer une commande de boisson - Domaine

**Contexte**
Le domaine doit appliquer les regles de cout des boissons selon leur type et garantir que le solde de jetons boisson ne devienne jamais negatif.

**Critères d'acceptation**

Feature: Domaine - Commander une boisson

Scenario: 1 - Commander une boisson non alcoolisee
Given un festivalier avec 0 jeton boisson
When il commande une boisson non alcoolisee
Then la commande est acceptee
And le solde de jetons boisson reste a 0

Scenario: 2 - Commander une boisson alcool normale
Given un festivalier avec 2 jetons boisson
When il commande une boisson alcool normale
Then la commande est acceptee
And le solde de jetons boisson devient 1

Scenario: 3 - Rejeter une boisson alcool premium sans solde suffisant
Given un festivalier avec 1 jeton boisson
When il commande une boisson alcool premium
Then la commande est rejetee
And le solde de jetons boisson reste a 1

**Notes**
- Une boisson non alcoolisee coute 0 jeton boisson.
- Une boisson alcool normale coute 1 jeton boisson.
- Une boisson alcool premium coute 2 jetons boisson.
