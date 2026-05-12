# Examiner une demande de modification de commande acquittee - Application

**Contexte**
La couche application expose au barman l'action de revue des demandes de modification sur commandes acquittees et retourne un resultat clair en cas d'acceptation ou de refus.

**Critères d'acceptation**

Feature: Application - Revoir une demande de modification acquittee

Scenario: 1 - Accepter une demande de changement et retourner le nouvel ETA
Given une demande de modification en attente pour une commande acquittee
When le barman appelle le endpoint de revue avec la decision d'acceptation
Then l'application retourne un succes
And la reponse contient le nouveau temps estime de preparation

Scenario: 2 - Refuser une demande de changement
Given une demande de modification en attente pour une commande acquittee
When le barman appelle le endpoint de revue avec la decision de refus
Then l'application retourne un succes
And la reponse indique que la demande est refusee avec la raison

Scenario: 3 - Retourner une erreur si la demande n'existe pas
Given aucun identifiant de demande correspondant n'existe
When le barman appelle le endpoint de revue
Then l'application retourne une erreur de ressource introuvable
And aucune mise a jour de commande n'est effectuee

**Notes**
- Le endpoint doit exposer explicitement la decision acceptation ou refus.
- En cas d'acceptation, le nouvel ETA doit etre visible dans la reponse.
