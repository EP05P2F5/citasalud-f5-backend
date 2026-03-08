Feature: Helper de login compartido

  Scenario: Obtener token JWT
    Given url baseUrl + '/auth/login'
    And request { nickname: '#(nickname)', password: '#(password)' }
    When method POST
    Then status 200
    * def token = response.token
