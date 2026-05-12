# Annuler une commande - Application

**Contexte**
La couche application expose la demande d'annulation, applique les regles d'etat de commande et renvoie une confirmation explicite avec les montants rembourses lorsque l'annulation est autorisee.

**Critères d'acceptation**

Feature: Application - Annuler une commande

Scenario: 1 - Annuler une commande non acquittee via API
Given une commande non acquittee appartenant au festivalier
When il appelle le endpoint d'annulation
Then l'application retourne un succes
And la reponse contient la confirmation d'annulation

Scenario: 2 - Retourner un conflit pour une commande deja acquittee
Given une commande deja acquittee appartenant au festivalier
When il appelle le endpoint d'annulation
Then l'application retourne une erreur de conflit
And le message indique que la commande ne peut plus etre annulee

Scenario: 3 - Exposer le detail des jetons rembourses
Given une commande non acquittee annulee avec succes
When l'application construit la reponse
Then la reponse inclut les montants rembourses par type de jetons
And ces montants correspondent au cout de la commande annulee

**Notes**
- L'API doit fournir une confirmation explicite de l'annulation.
- Le client doit pouvoir afficher les jetons restitues.
