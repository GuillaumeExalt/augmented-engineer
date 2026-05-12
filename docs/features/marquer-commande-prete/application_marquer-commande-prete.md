# Marquer une commande comme prete - Application

**Contexte**
La couche application expose l'action barman de finalisation de preparation et retourne un resultat explicite selon la disponibilite des articles prepares.

**Critères d'acceptation**

Feature: Application - Finaliser une commande prete

Scenario: 1 - Marquer une commande prete via API
Given une commande en preparation avec tous les articles prepares
When le barman appelle le endpoint de marquage prete
Then l'application retourne un succes
And la reponse contient l'etat prete

Scenario: 2 - Retourner une erreur si des articles manquent
Given une commande en preparation avec des articles non encore prepares
When le barman appelle le endpoint de marquage prete
Then l'application retourne une erreur de conflit
And le message indique que la preparation est insuffisante

Scenario: 3 - Confirmer l'envoi de notification de retrait
Given une commande est marquee prete avec succes
When l'application termine le traitement
Then une notification de retrait est declenchee
And la reponse confirme que le festivalier peut recuperer la commande

**Notes**
- Le endpoint doit etre reserve au role barman.
- La reponse doit rester explicite pour l'affichage cote client.
