# Infrastructure pour consulter le solde des jetons

**Contexte**
Un festivalier dispose de deux types de jetons : jetons boisson et jetons nourriture. Un festivalier peut avoir zéro ou plusieurs jetons boisson. Un festivalier peut avoir zéro ou plusieurs jetons nourriture. Un festivalier ne peut pas avoir un solde négatif de jetons. Un festivalier reçoit 9 jetons nourriture et 6 jetons boisson par jour de festival. Les jetons non dépensés ne sont pas reportés au jour suivant.

**Critères d'acceptation**

Feature: Infrastructure - Festivalier balance retrieval

Scenario: Retrieve festivalier balance successfully
Given a festivalier exists in the system with 7 food tokens and 4 drink tokens
When retrieving the festivalier by ID
Then the infrastructure returns the festivalier with correct balances

Scenario: Festivalier not found
Given a festivalier does not exist in the system
When retrieving the festivalier by ID
Then the infrastructure returns a not found error

Scenario: System error during retrieval
Given the storage system is unavailable
When retrieving the festivalier by ID
Then the infrastructure returns a system error

**Notes**
- Implémenter un repository pour récupérer le festivalier depuis le stockage (BDD ou API).
- Gérer les erreurs de connexion et de données manquantes.