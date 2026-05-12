# Passer une commande de boisson - Application

**Contexte**
La couche application expose le cas d'usage de commande de boisson, orchestre la validation du type de boisson et retourne une reponse claire selon le solde de jetons boisson du festivalier.

**Critères d'acceptation**

Feature: Application - Passer commande de boisson

Scenario: 1 - Soumettre une commande de boisson non alcoolisee
Given un festivalier avec identifiant 42 et 0 jeton boisson
When il appelle le endpoint de creation de commande avec une boisson non alcoolisee
Then l'application retourne un succes de creation
And la reponse indique que le cout en jetons boisson est 0

Scenario: 2 - Soumettre une commande de boisson alcool normale
Given un festivalier avec identifiant 42 et 3 jetons boisson
When il appelle le endpoint de creation de commande avec une boisson alcool normale
Then l'application retourne un succes de creation
And la reponse indique un debit de 1 jeton boisson

Scenario: 3 - Refuser une commande premium si le solde est insuffisant
Given un festivalier avec identifiant 42 et 1 jeton boisson
When il appelle le endpoint de creation de commande avec une boisson alcool premium
Then l'application retourne une erreur de validation
And le message explique que le solde de jetons boisson est insuffisant

Scenario: 4 - Commande creee avec succes
Given un festivalier identifie avec l'id "festivalier-42"
And les articles suivants sont disponibles :
	| article   | quantite |
	| Mojito    | 10       |
	| Eau plate | 50       |
When le festivalier envoie une requete POST /commandes avec :
	| festivalierId | festivalier-42                |
	| articles      | [{id: "mojito", quantite: 2}] |
Then la reponse a le statut HTTP 201
And la reponse contient un champ "commandeId" non vide
And la commande est creee avec le statut "EN_ATTENTE"

Scenario: 5 - Requete refusee si le festivalier n'est pas authentifie
Given aucun festivalier authentifie
When une requete POST /commandes est envoyee
Then la reponse a le statut HTTP 401

Scenario: 6 - Requete refusee si le corps de la requete est invalide
Given un festivalier identifie
When le festivalier envoie une requete POST /commandes sans articles
Then la reponse a le statut HTTP 400

Scenario: 7 - Requete refusee quand le stock disponible est insuffisant
Given un festivalier authentifie avec l'id "festivalier-42"
And les articles suivants sont disponibles :
	| article | quantite |
	| Mojito  | 1        |
When le festivalier envoie une requete POST /commandes avec :
	| festivalierId | festivalier-42                |
	| articles      | [{id: "mojito", quantite: 2}] |
Then l'application retourne une erreur de validation
And le message indique "STOCK_INSUFFISANT"

Scenario: 8 - Requete refusee si l'article demande n'existe pas au catalogue
Given un festivalier authentifie avec l'id "festivalier-42"
And aucun article "Champagne" n'est disponible au catalogue
When le festivalier envoie une requete POST /commandes avec :
	| festivalierId | festivalier-42                   |
	| articles      | [{id: "champagne", quantite: 1}] |
Then l'application retourne une erreur de validation
And le message indique "ARTICLE_INCONNU"

Scenario: 9 - Consulter une commande creee avec toutes ses lignes
Given une commande de boisson existe pour le festivalier "festivalier-42"
And elle contient les lignes suivantes :
	| article   | quantite |
	| Mojito    | 2        |
	| Eau plate | 1        |
When le festivalier demande cette commande par son identifiant
Then la reponse a le statut HTTP 200
And la reponse contient le statut "EN_ATTENTE"
And la reponse contient les deux lignes de commande

Scenario: 10 - Retourner une commande prete apres mise a jour de statut
Given une commande de boisson existante est enregistree avec le statut "EN_ATTENTE"
When le barman demande son marquage comme prete
Then la reponse a le statut HTTP 200
And la reponse contient le statut "PRETE"

Scenario: 11 - Lister les commandes en attente d'un festivalier
Given le festivalier "festivalier-42" possede 3 commandes dont 2 au statut "EN_ATTENTE"
When il demande la liste de ses commandes en attente
Then la reponse a le statut HTTP 200
And elle contient exactement 2 commandes

**Notes**
- Le contrat d'entree doit distinguer boisson non alcoolisee, alcool normale et alcool premium.
- Les messages d'erreur doivent etre explicites pour l'utilisateur.
- Une commande simple sur un article disponible reste valable pour toute quantite strictement positive.
