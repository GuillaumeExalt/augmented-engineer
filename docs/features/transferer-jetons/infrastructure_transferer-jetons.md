# Transferer des jetons a un autre festivalier - Infrastructure

**Contexte**
L'infrastructure doit persister les demandes de transfert, puis appliquer les mouvements de debit et credit de facon atomique lors de la confirmation.

**Critères d'acceptation**

Feature: Infrastructure - Persistance des transferts de jetons

Scenario: 1 - Persister une demande de transfert en attente
Given une demande de transfert validee en domaine
When l'infrastructure enregistre la demande
Then la demande est persistée avec l'etat en attente
And les montants proposes sont conserves pour la confirmation

Scenario: 2 - Appliquer debit et credit de facon atomique a la confirmation
Given une demande de transfert en attente est confirmee
When l'infrastructure applique les mouvements de jetons
Then le debit de l'emetteur et le credit du destinataire sont persistés dans la meme transaction
And aucun etat partiel n'est visible

Scenario: 3 - Notifier le destinataire puis tracer la confirmation
Given une demande de transfert est creee puis confirmee
When l'infrastructure publie les evenements associes
Then le destinataire recoit la demande de confirmation
And les deux festivaliers recoivent la confirmation finale

**Notes**
- La coherence transactionnelle est critique pour eviter les doubles credits.
- La tracabilite des transferts est necessaire pour audit et support.
