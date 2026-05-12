# Marquer une commande comme prete - Infrastructure

**Contexte**
L'infrastructure doit verifier les donnees de preparation, persister l'etat prete et emettre la notification de retrait sans laisser d'etat partiel en cas d'erreur.

**Critères d'acceptation**

Feature: Infrastructure - Persistance de commande prete

Scenario: 1 - Persister l'etat prete apres verification des articles prepares
Given une commande en preparation est eligible au statut prete
When l'infrastructure valide les quantites preparees et met a jour la commande
Then l'etat prete est persisté avec succes
And la date de disponibilite est enregistrée

Scenario: 2 - Ne pas persister l'etat prete si les donnees de preparation sont insuffisantes
Given les donnees de preparation ne couvrent pas toute la commande
When l'infrastructure tente la mise a jour
Then la mise a jour est refusee
And la commande conserve son etat precedent

Scenario: 3 - Emettre la notification de retrait de maniere fiable
Given une commande a ete persistee a l'etat prete
When l'infrastructure publie l'evenement de notification
Then le festivalier recoit la notification de retrait
And la publication est tracée pour audit

**Notes**
- La verification des quantites preparees doit se faire avant la transition d'etat.
- Les notifications doivent etre robustes aux erreurs de transport.
