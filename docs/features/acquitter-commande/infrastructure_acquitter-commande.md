# Acquitter une commande et fournir un temps estime - Infrastructure

**Contexte**
L'infrastructure doit persister l'acquittement et l'ETA de la commande, puis envoyer la notification au festivalier de facon fiable.

**Critères d'acceptation**

Feature: Infrastructure - Persistance de l'acquittement de commande

Scenario: 1 - Persister l'etat en preparation et l'ETA
Given une commande acquittee en domaine avec un temps estime calcule
When l'infrastructure enregistre la commande
Then l'etat en preparation est persisté
And la valeur d'ETA est persistée avec la commande

Scenario: 2 - Envoyer la notification de prise en charge
Given une commande acquittee est persistée avec succes
When l'infrastructure publie les evenements de notification
Then le festivalier recoit un message indiquant que la commande est en preparation
And le message contient le temps estime

Scenario: 3 - Eviter une mise a jour partielle en cas d'erreur technique
Given une erreur survient entre la persistance et la publication de notification
When l'infrastructure traite la transaction d'acquittement
Then la coherence de l'etat commande et de l'evenement est preservee
And l'application recoit une erreur technique exploitable

**Notes**
- Utiliser un mecanisme fiable de publication d'evenement pour eviter les pertes de notification.
- La tracabilite de l'ETA persisté est necessaire pour suivi operationnel.
