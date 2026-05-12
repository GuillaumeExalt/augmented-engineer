# Mutualiser des jetons pour une commande de groupe - Domaine

**Contexte**
Le domaine doit permettre a un groupe de festivaliers de mutualiser leurs jetons pour passer une commande unique, uniquement si la somme des contributions couvre le cout total de la commande.

**Critères d'acceptation**

Feature: Domaine - Commande de groupe avec mutualisation des jetons

Scenario: 1 - Accepter une commande de groupe avec contributions suffisantes
Given un groupe de 3 festivaliers avec des contributions valides en jetons boisson et nourriture
When ils soumettent une commande de groupe dont le cout total est couvert
Then la commande de groupe est acceptee
And les contributions de chaque festivalier sont deduites selon le montant annonce

Scenario: 2 - Rejeter une commande de groupe si le total mutualise est insuffisant
Given un groupe de festivaliers dont la somme des contributions est inferieure au cout total
When ils soumettent la commande de groupe
Then la commande est rejetee
And aucun jeton n'est deduit

Scenario: 3 - Rejeter une contribution superieure au solde d'un festivalier
Given un festivalier propose une contribution superieure a son solde disponible
When la commande de groupe est validee
Then la commande est rejetee
And les soldes de tous les festivaliers restent inchanges

**Notes**
- Une commande de groupe suit les memes regles de cout qu'une commande classique.
- Chaque contribution peut etre de n'importe quelle quantite tant qu'elle reste valide.
