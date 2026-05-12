# Passer une commande de boisson - Infrastructure

**Contexte**
L'infrastructure persiste les commandes de boisson et les mouvements de jetons associes, tout en garantissant l'integrite des donnees en cas d'echec technique.

**Critères d'acceptation**

Feature: Infrastructure - Commande boisson et debit des jetons

Scenario: 1 - Recuperer la categorie de boisson pour calculer le cout
Given un article boisson reference dans le catalogue
When l'infrastructure charge ses metadonnees
Then elle retourne son type de boisson attendu
And le calculateur de cout peut appliquer la regle de jetons

Scenario: 2 - Persister une commande validee et son debit de jetons
Given une commande boisson validee en domaine
When l'infrastructure enregistre la commande et le mouvement de jetons
Then la commande est stockee avec succes
And le debit de jetons boisson est stocke dans le meme flux transactionnel

Scenario: 3 - Ne pas persister de debit partiel en cas d'erreur
Given une erreur technique survient pendant l'enregistrement de la commande
When l'infrastructure termine le traitement
Then aucun debit de jetons partiel n'est conserve
And l'application recoit une erreur technique exploitable

Scenario: 4 - Creer une commande en attente pour une requete valide
Given un festivalier identifie avec l'id "festivalier-42"
And les articles "Mojito" et "Eau plate" sont disponibles dans le catalogue
When l'infrastructure cree la commande initiale pour 2 "Mojito"
Then la commande stockee porte le statut "EN_ATTENTE"
And un identifiant de commande non vide est genere

Scenario: 5 - Refuser la creation de commande si le festivalier n'est pas identifie
Given aucune authentification festivalier exploitable n'est fournie
When l'infrastructure traite la creation de commande pour 1 "Mojito"
Then l'infrastructure retourne une erreur de validation technique
And aucune commande n'est stockee

Scenario: 6 - Refuser la creation de commande si le corps de la requete est invalide
Given une demande de commande sans article exploitable
When l'infrastructure cree la commande initiale
Then l'infrastructure retourne une erreur de validation technique
And aucune commande n'est stockee

Scenario: 7 - Refuser la creation de commande quand le stock disponible est insuffisant
Given l'article "Mojito" dispose d'une quantite disponible de 1
When l'infrastructure cree la commande initiale pour 2 "Mojito"
Then l'infrastructure retourne une erreur de validation technique
And aucun stock n'est decremente

Scenario: 8 - Refuser la creation de commande si l'article demande n'existe pas au catalogue
Given aucun article "Champagne" n'est disponible dans le catalogue technique
When l'infrastructure cree la commande initiale pour 1 "Champagne"
Then l'infrastructure retourne une erreur de validation technique
And aucune commande n'est stockee

Scenario: 9 - Sauvegarder une nouvelle commande
Given une commande avec le statut "EN_ATTENTE" et deux lignes :
	| article   | quantite |
	| Mojito    | 2        |
	| Eau plate | 1        |
When l'infrastructure sauvegarde la commande
Then la commande peut etre retrouvee par son identifiant
And elle a le statut "EN_ATTENTE"
And elle contient bien les deux lignes de commande

Scenario: 10 - Mettre a jour le statut d'une commande
Given une commande sauvegardee avec le statut "EN_ATTENTE"
When l'infrastructure met a jour son statut a "PRETE"
Then en retrouvant la commande par son identifiant, elle a le statut "PRETE"

Scenario: 11 - Retrouver les commandes en attente d'un festivalier
Given 3 commandes pour le festivalier "festivalier-42" :
	| statut     |
	| EN_ATTENTE |
	| EN_ATTENTE |
	| PRETE      |
When l'infrastructure cherche les commandes avec le statut "EN_ATTENTE" pour "festivalier-42"
Then elle obtient exactement 2 commandes

**Notes**
- Les ecritures commande et jetons doivent rester coherentes.
- Les erreurs techniques doivent etre remontees sans masquer la cause fonctionnelle.
- Une commande simple sur un article disponible reste valable pour toute quantite strictement positive.
