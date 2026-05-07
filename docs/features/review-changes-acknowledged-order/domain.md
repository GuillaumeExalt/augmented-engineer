# Implement domain logic for reviewing and approving changes to acknowledged order

**Contexte**
The domain layer must allow bartender to review change requests for acknowledged orders, approve if possible by transferring prepared items, update time.

**Critères d'acceptation**
Feature: Review Changes
  Scenario: Approve changes if transferable
    Given change request for acknowledged order
    And prepared items can be transferred
    When approving
    Then changes applied, new time notified

  Scenario: Reject if not transferable
    Given request
    And cannot transfer
    When reviewing
    Then rejected

**Notes**
- Check if prepared items can be reassigned.