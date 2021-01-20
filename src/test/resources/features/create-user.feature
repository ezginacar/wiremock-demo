Feature: create user


  Scenario: Create a user
    Given Update "createUser" file with below parameters
         |name  |Ezgi      |
         |salary|100.000$  |
         |age   |18        |

    When Send "post" request

    Then Response should include those:
      |name   |Ezgi                                |
      |salary |100.000$                            |
      |age    |18                                  |
      |message|Successfully! Record has been added.|





