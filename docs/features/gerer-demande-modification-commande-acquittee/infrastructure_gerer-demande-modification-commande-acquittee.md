# Examiner une demande de modification de commande acquittee - Infrastructure

**Contexte**
L'infrastructure doit charger les donnees de preparation necessaires a la decision du barman, persister le resultat de revue et notifier le festivalier en cas d'acceptation.

**Critères d'acceptation**

Feature: Infrastructure - Persistance de la revue de demande de modification

Scenario: 1 - Charger la transferabilite des articles prepares
Given une demande de modification en attente sur commande acquittee
When l'infrastructure recupere les donnees de preparation associees
Then elle fournit les informations necessaires pour evaluer la transferabilite
And ces informations sont reliees a la demande courante

Scenario: 2 - Persister la decision de revue et le nouvel ETA
Given le barman a decide d'accepter la demande
When l'infrastructure enregistre la decision
Then le statut de demande est persisté en accepte
And le nouveau temps estime est persisté avec la commande mise a jour

Scenario: 3 - Notifier le festivalier apres acceptation
Given une decision d'acceptation a ete persistee
When l'infrastructure publie l'evenement de notification
Then le festivalier recoit une notification avec le nouvel ETA
And la publication est tracée pour audit

**Notes**
- Les operations de persistance et publication doivent rester coherentes.
- Les donnees de transferabilite doivent etre historisees pour justification.
