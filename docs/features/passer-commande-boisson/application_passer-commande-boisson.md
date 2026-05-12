# Passer une commande de boisson - Application

**Contexte**
La couche application expose le cas d'usage de commande de boisson, orchestre la validation du type de boisson et retourne une reponse claire selon le solde de jetons boisson du festivalier.

**Critères d'acceptation**

Feature: Application - Passer commande de boisson

Scenario: 1 - Soumettre une commande de boisson non alcoolisee
Given un festivalier avec identifiant 42 et 0 jeton boisson
When il appelle le endpoint de creation de commande avec une boisson non alcoolisee
Then l'application retourne un succes de creation
And la reponse indique que le cout en jetons boisson est 0

Scenario: 2 - Soumettre une commande de boisson alcool normale
Given un festivalier avec identifiant 42 et 3 jetons boisson
When il appelle le endpoint de creation de commande avec une boisson alcool normale
Then l'application retourne un succes de creation
And la reponse indique un debit de 1 jeton boisson

Scenario: 3 - Refuser une commande premium si le solde est insuffisant
Given un festivalier avec identifiant 42 et 1 jeton boisson
When il appelle le endpoint de creation de commande avec une boisson alcool premium
Then l'application retourne une erreur de validation
And le message explique que le solde de jetons boisson est insuffisant

Scenario: 4 - Soumettre une commande simple avec un article disponible
Given un festivalier identifie
And l'article "Mojito" est disponible dans le catalogue
When il appelle le endpoint de creation de commande pour 1 "Mojito"
Then l'application retourne un succes de creation
And la reponse contient le statut "EN_ATTENTE"
And la reponse contient un identifiant de commande non vide

Scenario: 5 - Refuser une commande si le festivalier n'est pas identifie
Given aucun identifiant festivalier exploitable dans la requete
And un article "Mojito" disponible en stock
When il appelle le endpoint de creation de commande pour 1 "Mojito"
Then l'application retourne une erreur d'authentification
And le statut HTTP retourne est 401

Scenario: 6 - Refuser une commande si aucun article n'est fourni dans la requete
Given un festivalier identifie
And aucun article n'est fourni dans la requete
When il appelle le endpoint de creation de commande
Then l'application retourne une erreur de validation
And le statut HTTP retourne est 400

**Notes**
- Le contrat d'entree doit distinguer boisson non alcoolisee, alcool normale et alcool premium.
- Les messages d'erreur doivent etre explicites pour l'utilisateur.
- Une commande simple sur un article disponible reste valable pour toute quantite strictement positive.
