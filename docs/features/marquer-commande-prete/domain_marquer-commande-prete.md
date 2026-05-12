# Marquer une commande comme prete - Domaine

**Contexte**
Le domaine doit autoriser le passage a l'etat prete seulement si les articles prepares sont suffisants pour satisfaire la commande, puis notifier que le retrait est possible.

**Critères d'acceptation**

Feature: Domaine - Marquer une commande prete

Scenario: 1 - Marquer la commande prete lorsque tous les articles sont prepares
Given une commande en preparation avec tous les articles requis prepares
When le barman marque la commande comme prete
Then la commande passe a l'etat prete
And un evenement de notification est produit pour le festivalier

Scenario: 2 - Refuser le passage a prete si la preparation est insuffisante
Given une commande en preparation avec des articles manquants
When le barman tente de marquer la commande comme prete
Then le changement d'etat est rejete
And la commande reste en preparation

Scenario: 3 - Refuser le passage a prete depuis un etat invalide
Given une commande qui n'est pas en preparation
When le barman tente de la marquer comme prete
Then le changement d'etat est rejete
And aucune notification de retrait n'est emise

**Notes**
- La verification de disponibilite des articles prepares est obligatoire.
- Seules les transitions d'etat autorisees doivent etre acceptees.
