@deneme
Feature: Deneme Feature

  Scenario: 1 - first test

    Given I go login rest service

    Then the status is 200 in the response
    And the elements contains the followings in the response
      | result | Login Successfully |