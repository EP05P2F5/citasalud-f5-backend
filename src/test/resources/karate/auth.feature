Feature: Autenticación - /auth/login

  Background:
    * url baseUrl

  Scenario: Login exitoso con credenciales válidas
    Given path '/auth/login'
    And request { nickname: 'karate_user', password: 'Karate123!' }
    When method POST
    Then status 200
    And match response.token != null
    And match response.username == 'karate_user'
    And match response.role != null
    And match response.email != null

  Scenario: Login con password incorrecto retorna 401
    Given path '/auth/login'
    And request { nickname: 'karate_user', password: 'contraseña_incorrecta' }
    When method POST
    Then status 401
    And match response.error != null

  Scenario: Login con usuario inexistente retorna 401
    Given path '/auth/login'
    And request { nickname: 'usuario_que_no_existe', password: 'cualquier' }
    When method POST
    Then status 401

  Scenario: Login con body vacío retorna error
    Given path '/auth/login'
    And request {}
    When method POST
    Then status 401

  Scenario: El token generado en login tiene formato JWT (tres partes separadas por punto)
    Given path '/auth/login'
    And request { nickname: 'karate_user', password: 'Karate123!' }
    When method POST
    Then status 200
    * def token = response.token
    * assert token.split('.').length == 3
