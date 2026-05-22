# Implémenter la consultation du solde des jetons

**Contexte**
Un festivalier dispose de deux types de jetons : jetons boisson et jetons nourriture. Un festivalier peut avoir zéro ou plusieurs jetons boisson. Un festivalier peut avoir zéro ou plusieurs jetons nourriture. Un festivalier ne peut pas avoir un solde négatif de jetons. Un festivalier reçoit 9 jetons nourriture et 6 jetons boisson par jour de festival. Les jetons non dépensés ne sont pas reportés au jour suivant.

**Critères d'acceptation**

Feature: Consult token balance

Scenario: Consult balance of a festivalier
Given a festivalier with 7 food tokens and 4 drink tokens
When consulting the token balance
Then receive 7 for food tokens
And receive 4 for drink tokens

Scenario: Consult initial balance
Given a new festivalier
When consulting the token balance
Then receive 0 for food tokens
And receive 0 for drink tokens

Scenario: Consult balance after daily allocation
Given a festivalier with 0 food tokens and 0 drink tokens
When daily tokens are allocated
And consulting the token balance
Then receive 9 for food tokens
And receive 6 for drink tokens

**Notes**
- Implémenter un cas d'usage pour consulter le solde des jetons d'un festivalier.
- Le cas d'usage doit retourner les soldes actuels des deux types de jetons.