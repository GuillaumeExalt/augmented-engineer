# Mutualiser des jetons pour une commande de groupe - Application

**Contexte**
La couche application orchestre la soumission d'une commande de groupe, verifie la validite des contributions par festivalier et retourne un resultat coherent si le total mutualise couvre le cout de la commande.

**Critères d'acceptation**

Feature: Application - Passer une commande de groupe

Scenario: 1 - Soumettre une commande de groupe valide
Given une requete contenant plusieurs festivaliers et leurs contributions
When l'application traite une commande de groupe dont le cout total est couvert
Then l'application retourne un succes de creation
And la reponse detaille la contribution retenue pour chaque festivalier

Scenario: 2 - Retourner une erreur si le total mutualise est insuffisant
Given une requete de commande de groupe dont le total des contributions est insuffisant
When l'application traite la demande
Then l'application retourne une erreur de validation
And le message indique que la somme mutualisee ne couvre pas le cout total

Scenario: 3 - Retourner une erreur si un contributeur est inconnu
Given une requete de commande de groupe contenant un identifiant festivalier inexistant
When l'application traite la demande
Then l'application retourne une erreur de ressource introuvable
And la commande n'est pas creee

**Notes**
- Le contrat d'entree doit associer explicitement chaque contribution a un festivalier.
- Les erreurs doivent preciser la cause de rejet de la commande de groupe.
