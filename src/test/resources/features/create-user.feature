@standalone
Feature: create user with wiremock standalone


  Scenario: Create a user

    Given Start standalone mock with "createUser" map

    When Send request as standalone with below parameters:
         |firstname |$name         |
         |lastname  |$lastname     |

    Then Status code should be 200

    And Response should include those:
      |firstname   |$name             |
      |lastname    |$lastname         |
      |message     |The new user [$name $lastname] is created!!|





