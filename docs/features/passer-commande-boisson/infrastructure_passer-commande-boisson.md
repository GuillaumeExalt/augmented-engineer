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

Scenario: 4 - Creer une commande en attente pour un article disponible
Given un article "Mojito" disponible en stock dans le catalogue
And une demande de commande pour 1 "Mojito"
When l'infrastructure cree la commande initiale
Then la commande stockee porte le statut "EN_ATTENTE"
And un identifiant de commande non vide est genere

**Notes**
- Les ecritures commande et jetons doivent rester coherentes.
- Les erreurs techniques doivent etre remontees sans masquer la cause fonctionnelle.
