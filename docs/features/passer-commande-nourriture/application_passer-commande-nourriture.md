# Passer une commande de nourriture - Application

**Contexte**
La couche application orchestre le cas d'usage de commande de nourriture, distingue les types d'articles alimentaires et renvoie un resultat coherent selon le solde de jetons nourriture.

**Critères d'acceptation**

Feature: Application - Passer commande de nourriture

Scenario: 1 - Soumettre une commande de snack
Given un festivalier avec identifiant 42 et 1 jeton nourriture
When il appelle le endpoint de creation de commande avec un snack
Then l'application retourne un succes de creation
And la reponse indique un debit de 1 jeton nourriture

Scenario: 2 - Soumettre une commande de repas
Given un festivalier avec identifiant 42 et 5 jetons nourriture
When il appelle le endpoint de creation de commande avec un repas
Then l'application retourne un succes de creation
And la reponse indique un debit de 3 jetons nourriture

Scenario: 3 - Refuser un repas lorsque le solde est insuffisant
Given un festivalier avec identifiant 42 et 1 jeton nourriture
When il appelle le endpoint de creation de commande avec un repas
Then l'application retourne une erreur de validation
And le message explique que le solde de jetons nourriture est insuffisant

**Notes**
- Le contrat d'entree doit identifier le type d'article alimentaire.
- Le resultat doit exposer clairement le cout de la commande.
