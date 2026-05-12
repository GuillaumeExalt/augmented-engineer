# Transferer des jetons a un autre festivalier - Domaine

**Contexte**
Le domaine doit encadrer le transfert de jetons entre festivaliers avec limite de 3 jetons par type, confirmation obligatoire du destinataire, et garantie de non-negativite du solde de l'emetteur.

**Critères d'acceptation**

Feature: Domaine - Transferer des jetons

Scenario: 1 - Creer une demande de transfert dans les limites autorisees
Given un festivalier souhaite transferer 2 jetons boisson et 1 jeton nourriture
When il initie un transfert vers un autre festivalier
Then une demande de transfert en attente est creee
And aucun mouvement de jetons n'est applique avant confirmation

Scenario: 2 - Rejeter un transfert qui depasse la limite autorisee
Given un festivalier souhaite transferer 4 jetons boisson
When il initie la demande de transfert
Then la demande est rejetee
And aucun mouvement de jetons n'est applique

Scenario: 3 - Confirmer le transfert avec soldes valides
Given une demande de transfert en attente et un emetteur avec soldes suffisants
When le destinataire confirme le transfert
Then les jetons sont debites chez l'emetteur et credites chez le destinataire
And les soldes finaux restent non negatifs

Scenario: 4 - Rejeter la confirmation si le solde de l'emetteur est devenu insuffisant
Given une demande de transfert en attente mais le solde de l'emetteur a diminue entre-temps
When le destinataire confirme le transfert
Then la confirmation est rejetee
And aucun debit ou credit n'est applique

**Notes**
- Le transfert est limite a 3 jetons par type et par operation.
- La confirmation du destinataire est obligatoire.
