# Passer une commande de boisson - Domaine

**Contexte**
Le domaine doit appliquer les regles de cout des boissons selon leur type et garantir que le solde de jetons boisson ne devienne jamais negatif.

**Critères d'acceptation**

Feature: Domaine - Commander une boisson

Scenario: 1 - Commander une boisson non alcoolisee
Given un festivalier avec 0 jeton boisson
When il commande une boisson non alcoolisee
Then la commande est acceptee
And le solde de jetons boisson reste a 0

Scenario: 2 - Commander une boisson alcool normale
Given un festivalier avec 2 jetons boisson
When il commande une boisson alcool normale
Then la commande est acceptee
And le solde de jetons boisson devient 1

Scenario: 3 - Rejeter une boisson alcool premium sans solde suffisant
Given un festivalier avec 1 jeton boisson
When il commande une boisson alcool premium
Then la commande est rejetee
And le solde de jetons boisson reste a 1

Scenario: 4 - Commande confirmee quand le stock est suffisant
Given les articles suivants sont disponibles en stock :
	| article | quantite disponible |
	| Mojito  | 10                  |
When on tente de creer une commande pour 2 "Mojito"
Then la commande est creee avec le statut "EN_ATTENTE"
And le stock de "Mojito" est decremente de 2

Scenario: 5 - Rejeter une commande si le festivalier n'est pas identifie
Given aucun identifiant festivalier exploitable
When il demande la creation d'une commande pour 1 "Mojito"
Then la commande est rejetee
And aucune commande en attente n'est creee

Scenario: 6 - Rejeter une commande si aucun article n'est demande
Given un festivalier identifie
When il demande la creation d'une commande sans article
Then la commande est rejetee
And aucune commande en attente n'est creee

Scenario: 7 - Commande refusee quand le stock est insuffisant
Given les articles suivants sont disponibles en stock :
	| article | quantite disponible |
	| Mojito  | 1                   |
When on tente de creer une commande pour 2 "Mojito"
Then la commande est refusee
And une erreur de type "STOCK_INSUFFISANT" est levee
And le stock de "Mojito" est inchange

Scenario: 8 - Commande refusee si l'article n'existe pas au catalogue
Given un catalogue vide
When on tente de creer une commande pour 1 "Champagne"
Then la commande est refusee
And une erreur de type "ARTICLE_INCONNU" est levee

Scenario: 9 - Retrouver une commande creee avec toutes ses lignes
Given une commande de boisson existe pour le festivalier "festivalier-42"
And elle contient deux lignes de commande
When on la retrouve par son identifiant
Then la commande retrouvee conserve le statut "EN_ATTENTE"
And elle contient les deux lignes de commande

Scenario: 10 - Mettre a jour le statut persiste d'une commande vers PRETE
Given une commande de boisson en attente existe deja
When on persiste son statut "PRETE"
Then la commande retrouvee par son identifiant porte le statut "PRETE"

Scenario: 11 - Retrouver uniquement les commandes en attente d'un festivalier
Given le festivalier "festivalier-42" possede 3 commandes dont 2 en attente
When on recupere ses commandes au statut "EN_ATTENTE"
Then on obtient exactement 2 commandes

**Notes**
- Une boisson non alcoolisee coute 0 jeton boisson.
- Une boisson alcool normale coute 1 jeton boisson.
- Une boisson alcool premium coute 2 jetons boisson.
- Une commande simple sur un article disponible reste valable pour toute quantite strictement positive.
