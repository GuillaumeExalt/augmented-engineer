# Envoyer des notifications d'hydratation - Domaine

**Contexte**
Le domaine doit definir quand et a quelle frequence envoyer des rappels d'hydratation: toutes les heures en standard, toutes les 30 minutes pour les festivaliers ayant consomme plus de 3 boissons alcoolisees sur la derniere heure, uniquement entre 11h00 et 19h00.

**Critères d'acceptation**

Feature: Domaine - Regles de notifications d'hydratation

Scenario: 1 - Appliquer la frequence horaire standard dans la plage autorisee
Given il est 14h00 pendant le festival
And un festivalier a consomme 2 boissons alcoolisees dans la derniere heure
When le domaine evalue la cadence de notification
Then une notification est due toutes les 60 minutes
And le message est de type rappel d'hydratation

Scenario: 2 - Appliquer la frequence renforcee en cas de forte consommation
Given il est 16h00 pendant le festival
And un festivalier a consomme 4 boissons alcoolisees dans la derniere heure
When le domaine evalue la cadence de notification
Then une notification est due toutes les 30 minutes
And le message d'hydratation reste amical et responsable

Scenario: 3 - Ne pas notifier hors plage horaire
Given il est 20h00 pendant le festival
When le domaine evalue la cadence de notification
Then aucune notification n'est due
And aucun envoi n'est planifie

**Notes**
- La fenetre d'envoi autorisee est de 11h00 a 19h00 inclus.
- Le seuil de frequence renforcee est strictement superieur a 3 boissons alcoolisees par heure.
