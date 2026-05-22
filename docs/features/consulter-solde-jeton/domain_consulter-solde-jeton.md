# Modéliser les jetons du festivalier

**Contexte**
Un festivalier dispose de deux types de jetons : jetons boisson et jetons nourriture. Un festivalier peut avoir zéro ou plusieurs jetons boisson. Un festivalier peut avoir zéro ou plusieurs jetons nourriture. Un festivalier ne peut pas avoir un solde négatif de jetons. Un festivalier reçoit 9 jetons nourriture et 6 jetons boisson par jour de festival. Les jetons non dépensés ne sont pas reportés au jour suivant.

**Critères d'acceptation**

Feature: Festivalier tokens

Scenario: 1 - Initial balance
Given a new festivalier
When the festivalier is created
Then food token balance is 0
And drink token balance is 0

Scenario: 2 - Daily token allocation
Given a festivalier with 0 food tokens and 0 drink tokens
When daily tokens are allocated
Then food token balance becomes 9
And drink token balance becomes 6

Scenario: 3 - Consult current balance of a festivalier
Given a festivalier with 7 food tokens and 4 drink tokens
When consulting the token balance
Then receive 7 for food tokens
And receive 4 for drink tokens

Scenario: 4 - Tokens not carried over
Given a festivalier with 5 food tokens and 3 drink tokens
When daily tokens are allocated
Then food token balance becomes 9
And drink token balance becomes 6

Scenario: 5 - Non-negative balance
Given a festivalier
When attempting to set negative food tokens
Then an error is thrown
And drink token balance remains non-negative

**Notes**
- Modéliser la classe Festivalier avec les soldes des jetons.
- Implémenter une méthode pour allouer les jetons quotidiens.
- Assurer l'invariant de non-négativité.
