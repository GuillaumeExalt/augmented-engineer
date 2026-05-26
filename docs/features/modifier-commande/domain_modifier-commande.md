# Modifier une commande - Domaine

<!-- github-issue: GuillaumeExalt/augmented-engineer#8 -->

**Contexte**
Le domaine doit autoriser la modification d'une commande uniquement tant qu'elle n'est pas acquittee, tout en revalidant le cout total de la commande modifiee contre les soldes de jetons du festivalier.

**Critères d'acceptation**

Feature: Domaine - Modifier une commande

Scenario: 1 - Modifier une commande non acquittee
Given une commande non acquittee avec un festivalier ayant des soldes suffisants
When le festivalier ajoute ou retire des articles
Then la commande est mise a jour
And le nouveau cout respecte les soldes de jetons boisson et nourriture

Scenario: 2 - Rejeter une modification qui depasse les soldes
Given une commande non acquittee et un festivalier avec soldes limites
When le festivalier ajoute des articles qui depassent ses soldes
Then la modification est rejetee
And la commande conserve son contenu initial

Scenario: 3 - Traiter une commande deja acquittee comme une demande de changement
Given une commande deja acquittee par le barman
When le festivalier demande une modification
Then la commande n'est pas modifiee directement
And une demande de changement est creee pour revue par le barman

**Notes**
- Une commande acquittee ne peut plus etre modifiee en place.
- Toute modification d'une commande non acquittee doit revalider les couts.
