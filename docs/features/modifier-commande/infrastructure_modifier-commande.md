# Modifier une commande - Infrastructure

**Contexte**
L'infrastructure doit persister de facon coherente soit la mise a jour directe de la commande non acquittee, soit l'absence de mutation lorsque la modification est rejetee pour depassement de soldes, soit la creation d'une demande de changement associee a une commande deja acquittee.

**Critères d'acceptation**

Feature: Infrastructure - Persistance des modifications de commande

Scenario: 1 - Persister la mise a jour d'une commande non acquittee
Given une commande non acquittee validee pour modification
When l'infrastructure enregistre les nouvelles lignes de commande et les ajustements de jetons
Then les changements sont persistés dans une transaction unique
And l'etat final de la commande est coherent

Scenario: 2 - Ne pas persister une modification qui depasse les soldes
Given une commande non acquittee et une modification rejetee pour depassement des soldes
When l'infrastructure traite la tentative de mise a jour
Then aucune mutation de la commande n'est persistée
And la couche superieure recoit un rejet exploitable

Scenario: 3 - Persister une demande de changement pour commande acquittee
Given une commande acquittee et une demande de changement valide
When l'infrastructure enregistre la demande
Then la demande de changement est persistée avec un statut en attente
And un message est publie pour notifier le barman

**Notes**
- Les ecritures de commande, de jetons et de demande de changement doivent etre atomiques.
- Les rejets metier ne doivent laisser aucune mise a jour partielle sur la commande.
- Les notifications barman doivent etre tracables.
