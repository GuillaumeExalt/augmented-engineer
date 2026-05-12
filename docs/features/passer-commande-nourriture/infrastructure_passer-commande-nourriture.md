# Passer une commande de nourriture - Infrastructure

**Contexte**
L'infrastructure persiste les commandes de nourriture et les mouvements de jetons nourriture, avec des garanties de coherence en cas de panne ou d'erreur de stockage.

**Critères d'acceptation**

Feature: Infrastructure - Commande nourriture et debit des jetons

Scenario: 1 - Charger le type d'article alimentaire
Given un article de nourriture existe dans le catalogue
When l'infrastructure charge ses informations
Then elle retourne son type snack ou repas
And le calcul du cout peut etre applique correctement

Scenario: 2 - Persister la commande et le debit des jetons nourriture
Given une commande nourriture validee en domaine
When l'infrastructure enregistre la commande et le mouvement de jetons
Then la commande est stockee avec succes
And le debit de jetons nourriture est stocke dans le meme flux transactionnel

Scenario: 3 - Annuler les ecritures partielles en cas d'echec
Given une erreur de stockage survient pendant l'enregistrement
When l'infrastructure finalise le traitement
Then aucune ecriture partielle de debit n'est conservee
And l'application recoit une erreur technique exploitable

**Notes**
- La persistance des commandes et des jetons doit rester atomique.
- Les erreurs doivent etre journalisees et remontees proprement.
