# Acquitter une commande et fournir un temps estime - Application

<!-- github-issue: GuillaumeExalt/augmented-engineer#1 -->

**Contexte**
La couche application expose l'action d'acquittement pour le barman, retourne l'etat de preparation et l'ETA calcule, puis declenche la notification vers le festivalier.

**Critères d'acceptation**

Feature: Application - Acquitter une commande

Scenario: 1 - Acquitter une commande et retourner son ETA
Given une commande en attente d'acquittement
When le barman appelle le endpoint d'acquittement
Then l'application retourne un succes
And la reponse contient l'etat en preparation et le temps estime

Scenario: 2 - Retourner un ETA correct pour des boissons non alcoolisees de types differents
Given une commande contenant trois types de boissons non alcoolisees
When le barman appelle le endpoint d'acquittement
Then l'application retourne un temps estime de 3 minutes
And le festivalier est notifie du demarrage de preparation

Scenario: 3 - Refuser l'acquittement si la commande est deja en preparation
Given une commande deja acquittee
When le barman appelle le endpoint d'acquittement
Then l'application retourne une erreur de conflit
And aucune nouvelle notification n'est emise

**Notes**
- L'API doit exposer l'ETA calcule dans la reponse d'acquittement.
- Le message de notification doit indiquer que la commande est en cours de preparation.
