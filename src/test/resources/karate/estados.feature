Feature: Gestión de Estados - /estados

  Background:
    * url baseUrl
    * def loginRes = call read('classpath:karate/helpers/login-admin.js')
    * def authToken = loginRes.token

  Scenario: Listar todos los estados
    Given path '/estados'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response == '#[]'
    And match each response == { idEstado: '#number', descripcion: '#string' }

  Scenario: Listar estados sin autenticación retorna 401
    Given path '/estados'
    When method GET
    Then status 401

  Scenario: Obtener estado por ID 1 (Pendiente)
    Given path '/estados/1'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.idEstado == 1
    And match response.descripcion == 'Pendiente'

  Scenario: Obtener estado por ID 2 (En proceso)
    Given path '/estados/2'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.idEstado == 2
    And match response.descripcion == 'En proceso'

  Scenario: Obtener estado por ID inexistente retorna 404
    Given path '/estados/9999'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 404

  Scenario: Los estados predefinidos del sistema deben existir
    Given path '/estados'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    * def descripciones = $response[*].descripcion
    And match descripciones contains 'Pendiente'
    And match descripciones contains 'Resuelta'
