# Modifier une commande - Application

<!-- github-issue: GuillaumeExalt/augmented-engineer#7 -->

**Contexte**
La couche application expose la demande de modification de commande et applique le bon parcours selon l'etat de la commande: modification immediate si non acquittee, ou creation d'une demande de changement si deja acquittee.

**Critères d'acceptation**

Feature: Application - Demander la modification d'une commande

Scenario: 1 - Modifier une commande non acquittee via API
Given une commande non acquittee appartenant au festivalier
When il appelle le endpoint de modification avec des ajouts et retraits valides
Then l'application retourne un succes
And la reponse contient la commande mise a jour

Scenario: 2 - Creer une demande de changement pour une commande acquittee
Given une commande deja acquittee appartenant au festivalier
When il appelle le endpoint de modification
Then l'application retourne un statut indiquant une demande de changement en attente
And le barman est notifie de la demande

Scenario: 3 - Retourner une erreur si la modification depasse les soldes
Given une commande non acquittee et des soldes insuffisants
When le festivalier appelle le endpoint de modification avec un cout excessif
Then l'application retourne une erreur de validation
And la commande d'origine reste inchangee

**Notes**
- Le resultat doit distinguer clairement une modification appliquee d'une demande de changement.
- Les messages d'erreur doivent indiquer la raison du refus.
