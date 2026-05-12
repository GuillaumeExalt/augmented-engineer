# Examiner une demande de modification de commande acquittee - Domaine

**Contexte**
Le domaine doit permettre au barman d'accepter ou refuser une demande de modification sur commande deja acquittee, avec acceptation possible uniquement si au moins un article deja prepare peut etre transfere vers une autre commande.

**Critères d'acceptation**

Feature: Domaine - Revue d'une demande de modification acquittee

Scenario: 1 - Accepter la demande si un article prepare est transferable
Given une commande acquittee avec une demande de modification en attente
And au moins un article deja prepare peut etre transfere vers une autre commande
When le barman accepte la demande
Then la demande passe a l'etat acceptee
And un nouveau temps estime est calcule pour le festivalier

Scenario: 2 - Refuser la demande si aucun article prepare n'est transferable
Given une commande acquittee avec une demande de modification en attente
And aucun article deja prepare ne peut etre transfere
When le barman examine la demande
Then la demande est refusee
And la commande initiale reste inchangee

Scenario: 3 - Produire une notification metier lors d'une acceptation
Given une demande de modification est acceptee
When le domaine finalise la decision
Then un evenement metier de notification est produit
And cet evenement contient le nouveau temps estime

**Notes**
- La condition de transferabilite est obligatoire pour accepter.
- Le nouveau temps estime doit etre transmis au festivalier.
