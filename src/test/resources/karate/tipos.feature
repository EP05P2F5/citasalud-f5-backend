Feature: Gestión de Tipos - /tipos

  Background:
    * url baseUrl
    * def loginRes = call read('classpath:karate/helpers/login-admin.js')
    * def authToken = loginRes.token

  Scenario: Listar todos los tipos
    Given path '/tipos'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response == '#[]'
    And match each response == { idTipo: '#number', descripcion: '#string' }

  Scenario: Listar tipos sin autenticación retorna 401
    Given path '/tipos'
    When method GET
    Then status 401

  Scenario: Obtener tipo por ID 1 (Queja)
    Given path '/tipos/1'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.idTipo == 1
    And match response.descripcion == 'Queja'

  Scenario: Obtener tipo por ID 2 (Reclamo)
    Given path '/tipos/2'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.idTipo == 2
    And match response.descripcion == 'Reclamo'

  Scenario: Obtener tipo por ID inexistente retorna 404
    Given path '/tipos/9999'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 404

  Scenario: Los cuatro tipos PQRS deben existir
    Given path '/tipos'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    * def descripciones = $response[*].descripcion
    And match descripciones contains 'Queja'
    And match descripciones contains 'Reclamo'
    And match descripciones contains 'Sugerencia'
    And match descripciones contains 'Petición'
