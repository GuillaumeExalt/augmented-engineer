# Transferer des jetons a un autre festivalier - Application

**Contexte**
La couche application expose l'initiation et la confirmation du transfert de jetons, avec des reponses explicites sur l'etat de la demande et les motifs de rejet.

**Critères d'acceptation**

Feature: Application - Transferer des jetons entre festivaliers

Scenario: 1 - Initier un transfert avec succes
Given un emetteur avec identifiant 10 et un destinataire avec identifiant 11
When l'emetteur appelle le endpoint d'initiation avec des montants valides
Then l'application retourne un succes
And la reponse indique un transfert en attente de confirmation

Scenario: 2 - Confirmer un transfert en attente
Given un transfert en attente existe pour le destinataire
When le destinataire appelle le endpoint de confirmation
Then l'application retourne un succes
And la reponse indique que le transfert est complete

Scenario: 3 - Retourner une erreur sur depassement de limite ou solde insuffisant
Given une demande de transfert depasse la limite ou ne respecte pas les soldes
When l'emetteur ou le destinataire appelle le endpoint correspondant
Then l'application retourne une erreur de validation
And le message indique la regle violee

**Notes**
- L'API doit distinguer clairement les etats en attente, confirme et refuse.
- Les erreurs doivent etre comprehensibles pour les deux festivaliers.
