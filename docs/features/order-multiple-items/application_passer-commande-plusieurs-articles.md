# Passer une commande de plusieurs articles - Application

**Contexte**
La couche application orchestre la soumission d'une commande multi-articles et retourne une erreur explicite si le cout total depasse le solde de jetons boisson ou nourriture.

**Critères d'acceptation**

Feature: Application - Passer commande de plusieurs articles

Scenario: 1 - Soumettre une commande multi-articles valide
Given un festivalier avec identifiant 42 ayant des soldes suffisants
When il appelle le endpoint de creation de commande avec une boisson alcool normale et un snack
Then l'application retourne un succes de creation
And la reponse expose les soldes de jetons mis a jour

Scenario: 2 - Retourner une erreur si le solde boisson est insuffisant
Given un festivalier avec identifiant 42 et 0 jeton boisson
When il appelle le endpoint de creation de commande avec une boisson alcool premium
Then l'application retourne une erreur de validation
And le message indique une insuffisance de jetons boisson

Scenario: 3 - Retourner une erreur si le solde nourriture est insuffisant
Given un festivalier avec identifiant 42 et 1 jeton nourriture
When il appelle le endpoint de creation de commande avec un repas et un snack
Then l'application retourne une erreur de validation
And le message indique une insuffisance de jetons nourriture

**Notes**
- La charge utile doit accepter plusieurs lignes de commande dans une seule requete.
- Les erreurs doivent identifier le type de jetons en depassement.
