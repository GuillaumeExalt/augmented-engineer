---
agent: agent
name: TDD Red step
description: This prompt is used to implement one test scenario that fails in a TDD workflow for an AI agent
argument-hint: Implement the following test scenario in a TDD workflow for an AI agent: {scenario_description}
tools: ['execute/getTerminalOutput', 'execute/runInTerminal', 'read/problems', 'read/readFile', 'read/terminalSelection', 'read/terminalLastCommand', 'edit/createDirectory', 'edit/createFile', 'edit/editFiles', 'search', 'upstash/context7/*', 'todo']
model: GPT-5.4 (copilot)
---

# Red TDD step prompt

## Instructions
1. Analyze the provided scenario description carefully.
    - If the scenario description is provided as an issue reference, retrieve the issue content and extract the specified scenario.
    - If the scenario description is provided directly, use it as is.
    - If the scenario already exists, create and maintain the corresponding test suite for each architecture layer (application, domain, and infrastructure) by using the existing files following the naming convention: application_{feature_name}.md, domain_{feature_name}.md, and infrastructure_{feature_name}.md.
    - If the scenario does not already exist, first analyze the missing behavior and complete the related issues for each architecture layer (application, domain, and infrastructure) in a new scenario. Once the implementation details and issues are fully defined, create the corresponding tests for each layer to ensure the new scenario is properly covered.
2. Check if a test file already exists for the scope of this test scenario.
   - If it exists, append the new test case to the existing file.
   - If it does not exist, create a new test file in the appropriate directory structure based on the module (domain, application, infrastructure).
3. Write the test case so it accurately reflects the scenario and is expected to fail initially. You MUST follow the testing guidelines for the module you are currently working on:
    - for the domain module, follow the guidelines in `docs/agents/instructions/domain-testing.instructions.md`
    - for the application module, follow the guidelines in `docs/agents/instructions/application-testing.instructions.md`
    - for the infrastructure module, follow the guidelines in `docs/agents/instructions/infrastructure-testing.instructions.md`
4. Run the test to confirm it fails.

## Requirements
- You **MUST** follow the guidelines for the module you are currently working on.
- **NEVER** implement any production code in this step. Your ONLY goal is to write a failing test.
- You **MUST** ensure the test fails when executed.
- The name of the test method should be descriptive and follow the naming conventions outlined in the testing guidelines.

## Routing and CRITICAL rules
- If the input references an issue in `docs/features/{feature}/`, determine the appropriate module from the issue filename or the scenario context. If ambiguous, prefer `domain` and ask one clarifying question.
- CRITICAL: Do not produce or modify production code. If a code snippet is required for the test harness, place it under `tests/` or `tests_helpers/` only and mark it as a test double.
- CRITICAL: If the agent is tempted to implement business logic to make the test pass, refuse and only create the test file.

## Naming conventions and examples
- Test file naming: for domain tests use `domain/src/test/.../XxxTest.java` or for lightweight Python tests use `tests/tdd/test_<feature>_<case>.py`.
- Test method naming: `should<ExpectedBehavior>When<Condition><ScenarioNumberX>` (e.g., `shouldCreateOrderWhenItemAvailable`).
- If the test already exists, rename him with the same convention and add a scenario number at the end if needed (e.g., `shouldCreateOrderWhenItemAvailableScenario2`).

## Additional negative examples
- Do NOT merge multiple modules in a single test file when the issue was split per module. Generate one test per module scope.
- Do NOT add production classes under `domain/src/main`/`application/src/main`/`infrastructure/src/main` in this step.

## Examples

### Domain test example : file does not yet exist

Input :
Scenario Description: Scenario: Successfully export contacts
Given a user with 20 contacts
When executing a query to fetch contacts
Then the system retrieves all 20 contacts and generates an export DTO

Expected Output :

- a new file `domain/src/test/java/com/example/domain/contact/ContactExportUseCaseTest.java` is created with the following content :

```java
package com.example.domain.contact;

import com.example.domain.test.fixture.ContactExportFixture;
import com.example.domain.test.state.TestState;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;

class ContactExportUseCaseTest {

    private ContactExportFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new ContactExportFixture();
    }

    @Test
    void shouldProduceExportDtoWhenContactsExist() {
        // Given a user with 20 contacts
        TestState<User> userTestState = fixture.getUserTestState();
        TestState<Contact> contactTestState = fixture.getContactTestState();
        UseCaseHandler<ExportContactQuery, ContactExportDto> handler = fixture.getUseCaseHandler();

        User user = new User("user1");
        userTestState.add(user);
        List<Contact> contacts = IntStream.range(0, 20)
                .mapToObj(i -> new Contact("Contact " + i, "contact" + i + "@example.com"))
                .collect(Collectors.toList());
        contacts.forEach(contactTestState::add);

        // When executing a query to fetch contacts
        ExportContactQuery query = new ExportContactQuery(user.getId());
        ContactExportDto exportDto = handler.execute(query);

        // Then the system retrieves all 20 contacts and generates an export DTO
        assertThat(exportDto).isNotNull();
        assertThat(exportDto.getContacts()).hasSize(20);
    }
}
```

## Negative examples and tips
- Do not implement production code in this prompt: only tests that initially fail.
- If the scenario is ambiguous, ask 1–2 clarifying questions before writing the test.
- Prefer concise, focused test cases that exercise one behavior at a time.

## References
- `templates/issue.md`
- `scripts/validate_issue_format.py`
- `docs/features/` (generated issues with Gherkin scenarios)

<!-- Notes personnelles :

J'ai ajouté : - If the scenario already exists, create and maintain the corresponding test suite for each architecture layer (application, domain, and infrastructure) by using the existing files following the naming convention: application_{feature_name}.md, domain_{feature_name}.md, and infrastructure_{feature_name}.md.

Pour pouvoir completer les issues et ajouter des scenario de tests, ce qui peux être très utile pour faire évoluer les features et les tests au fur et à mesure de l'avancement du projet.

- If the scenario does not already exist, first analyze the missing behavior and complete the related issues for each architecture layer (application, domain, and infrastructure) in a new scenario. Once the implementation details and issues are fully defined, create the corresponding tests for each layer to ensure the new scenario is properly covered.

Pour créer des tests dans chaque couche même si le scénario n'existe, c'est peut être un peu trop. J'ai tendance à mettre des tests uniquement dans la couche ou le comportement est implémenté, mais ça peux être intéressant de créer les tests dans les autres couches pour forcer l'implémentation à être plus complète et éviter d'oublier des cas de tests et vu que cela ne prend pas plus de temps. A voir à l'usage si c'est pertinent ou pas.-->