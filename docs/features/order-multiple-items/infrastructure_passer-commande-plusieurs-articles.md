# Passer une commande de plusieurs articles - Infrastructure

**Contexte**
L'infrastructure doit persister une commande multi-articles et les mouvements de jetons associes en garantissant l'atomicite, pour eviter toute deduction partielle en cas d'erreur.

**Critères d'acceptation**

Feature: Infrastructure - Persistance d'une commande multi-articles

Scenario: 1 - Enregistrer une commande et ses debits de jetons
Given une commande multi-articles validee en domaine
When l'infrastructure persiste la commande et les mouvements de jetons boisson et nourriture
Then la commande est stockee avec ses lignes detaillees
And les debits de jetons correspondants sont stockes dans la meme transaction

Scenario: 2 - Eviter toute persistance partielle en cas d'echec
Given une erreur technique survient pendant l'enregistrement d'un des mouvements de jetons
When l'infrastructure cloture la transaction
Then aucune ligne de commande ni debit partiel n'est conserve
And l'application recoit une erreur technique exploitable

Scenario: 3 - Rejeter une commande avec article inconnu du catalogue
Given une commande reference un article absent du catalogue technique
When l'infrastructure tente de resoudre les metadonnees de cet article
Then l'infrastructure retourne une erreur de donnee introuvable
And aucune ecriture de commande n'est persistée

**Notes**
- Les ecritures commande et jetons doivent etre atomiques.
- Le mapping article vers type de jetons doit etre centralise et fiable.
