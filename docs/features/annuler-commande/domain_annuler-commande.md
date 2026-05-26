# Annuler une commande - Domaine

<!-- github-issue: GuillaumeExalt/augmented-engineer#5 -->

**Contexte**
Le domaine doit autoriser l'annulation d'une commande uniquement tant qu'elle n'est pas acquittee, puis rembourser les jetons utilises au solde du festivalier.

**Critères d'acceptation**

Feature: Domaine - Annuler une commande

Scenario: 1 - Annuler une commande non acquittee et rembourser les jetons
Given une commande non acquittee avec des jetons deja deduits
When le festivalier annule la commande
Then la commande passe a l'etat annulee
And les jetons boisson et nourriture utilises sont rembourses

Scenario: 2 - Refuser l'annulation d'une commande deja acquittee
Given une commande deja acquittee
When le festivalier demande son annulation
Then l'annulation est rejetee
And aucun remboursement n'est applique

Scenario: 3 - Conserver un resultat stable sur une commande deja annulee
Given une commande deja annulee
When le festivalier demande a nouveau l'annulation
Then le domaine retourne un resultat idempotent
And aucun mouvement de jetons supplementaire n'est applique

**Notes**
- Le remboursement restitue uniquement les jetons de la commande annulee.
- Les transitions d'etat doivent rester coherentes.
