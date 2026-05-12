# Envoyer des notifications d'hydratation - Infrastructure

**Contexte**
L'infrastructure doit collecter les donnees de consommation alcoolisee, executer les planifications d'envoi et distribuer les notifications avec tracabilite.

**Critères d'acceptation**

Feature: Infrastructure - Distribution des notifications d'hydratation

Scenario: 1 - Recuperer la consommation alcoolisee de la derniere heure
Given un historique de commandes de boissons est disponible
When l'infrastructure calcule la consommation alcoolisee par festivalier sur 60 minutes
Then elle retourne un compteur exploitable par les regles de cadence
And les donnees sont horodatees

Scenario: 2 - Envoyer les notifications avec message amical
Given une liste de notifications a envoyer est fournie par l'application
When l'infrastructure publie les messages vers le canal de notification
Then chaque festivalier cible recoit le rappel d'hydratation
And le contenu inclut un message de consommation responsable

Scenario: 3 - Journaliser les envois et les echecs
Given une campagne de notifications est en cours
When l'infrastructure termine les envois
Then chaque envoi reussi est journalise avec horodatage
And chaque echec est journalise avec la cause pour reprise

**Notes**
- Les journaux d'envoi sont necessaires pour suivi operationnel et audit.
- Le composant de planification doit rester coherent avec la fenetre 11h00-19h00.
