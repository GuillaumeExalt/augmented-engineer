# Envoyer des notifications d'hydratation - Application

**Contexte**
La couche application orchestre l'execution periodique des rappels d'hydratation, applique la cadence personnalisee selon la consommation recente et garantit le respect de la plage horaire autorisee.

**Critères d'acceptation**

Feature: Application - Orchestrer les notifications d'hydratation

Scenario: 1 - Declencher la campagne horaire entre 11h00 et 19h00
Given il est 12h00 un jour de festival
When le scheduler applicatif lance le cas d'usage d'hydratation
Then l'application prepare les notifications pour tous les festivaliers eligibles
And chaque notification contient un message amical de consommation responsable

Scenario: 2 - Augmenter la frequence pour les profils a risque
Given un festivalier a consomme plus de 3 boissons alcoolisees sur la derniere heure
When l'application calcule le prochain envoi
Then l'intervalle retenu est de 30 minutes
And le festivalier est inclus dans les envois renforces

Scenario: 3 - Ignorer les executions hors plage
Given il est 09h30 un jour de festival
When le scheduler applicatif lance le cas d'usage d'hydratation
Then l'application ne prepare aucune notification
And un statut indiquant hors plage horaire est retourne

**Notes**
- Le scheduler doit etre deterministic et testable.
- Les regles de cadence doivent etre centralisees dans le cas d'usage.
