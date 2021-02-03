@standalone
Feature: create user with wiremock standalone


  Scenario: Create a user

    Given Start standalone mock with "createUser" map

    When Send request as standalone with below parameters:
         |firstname |Ezgi         |
         |lastname  |Nacar        |

    Then Status code should be 200

    And Response should include elements:
      |id     |
      |message|

    And Response should include those informations:
      |data.message     |A new user [Ezgi Nacar] is created!!|
      |status           |success                             |





