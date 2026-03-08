Feature: Gestión de PQRS - /pqrs

  Background:
    * url baseUrl
    # Login con usuario normal para pruebas de creación
    * def loginUser = call read('classpath:karate/helpers/login-user.js')
    * def userToken = loginUser.token
    # Login con admin para pruebas de administración
    * def loginAdmin = call read('classpath:karate/helpers/login-admin.js')
    * def adminToken = loginAdmin.token

  Scenario: Listar todas las PQRS requiere autenticación
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Listar PQRS sin token retorna 401
    Given path '/pqrs'
    When method GET
    Then status 401

  Scenario: Crear una PQRS con datos válidos retorna 201
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 1,
        "estadoId": 1,
        "descripcion": "Esta es una queja de prueba creada por Karate"
      }
      """
    When method POST
    Then status 201
    And match response.idPqrs == '#number'
    And match response.descripcion == 'Esta es una queja de prueba creada por Karate'
    And match response.radicado == '#string'
    * def pqrsId = response.idPqrs

  Scenario: Crear PQRS sin descripción retorna 400
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 1,
        "estadoId": 1
      }
      """
    When method POST
    Then status 400

  Scenario: Crear PQRS sin tipoId retorna 400
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "estadoId": 1,
        "descripcion": "Falta el tipo"
      }
      """
    When method POST
    Then status 400

  Scenario: Crear PQRS sin estadoId retorna 400
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 2,
        "descripcion": "Falta el estado"
      }
      """
    When method POST
    Then status 400

  Scenario: Obtener PQRS recién creada por ID
    # Crear primero
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 2,
        "estadoId": 1,
        "descripcion": "Reclamo de prueba para obtener por ID"
      }
      """
    When method POST
    Then status 201
    * def nuevaPqrsId = response.idPqrs
    # Obtener por ID
    Given path '/pqrs/' + nuevaPqrsId
    And header Authorization = 'Bearer ' + userToken
    When method GET
    Then status 200
    And match response.idPqrs == nuevaPqrsId
    And match response.descripcion == 'Reclamo de prueba para obtener por ID'

  Scenario: Obtener PQRS por ID inexistente retorna 404
    Given path '/pqrs/999999'
    And header Authorization = 'Bearer ' + userToken
    When method GET
    Then status 404

  Scenario: Obtener PQRS sin token retorna 401
    Given path '/pqrs/1'
    When method GET
    Then status 401

  Scenario: Crear PQRS de tipo Sugerencia
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 3,
        "estadoId": 1,
        "descripcion": "Sugerencia: mejorar el sistema de tickets"
      }
      """
    When method POST
    Then status 201
    And match response.tipo.descripcion == 'Sugerencia'

  Scenario: Crear PQRS de tipo Petición
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 4,
        "estadoId": 1,
        "descripcion": "Petición de información sobre el proceso"
      }
      """
    When method POST
    Then status 201
    And match response.tipo.descripcion == 'Petición'

  Scenario: PQRS nueva debe tener estado Pendiente
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 1,
        "estadoId": 1,
        "descripcion": "Queja que debe quedar pendiente"
      }
      """
    When method POST
    Then status 201
    And match response.estado.descripcion == 'Pendiente'

  Scenario: PQRS nueva debe tener radicado generado automáticamente
    Given path '/pqrs'
    And header Authorization = 'Bearer ' + userToken
    And request
      """
      {
        "tipoId": 1,
        "estadoId": 1,
        "descripcion": "Verificar radicado automático"
      }
      """
    When method POST
    Then status 201
    * def radicado = response.radicado
    * assert radicado != null && radicado.length > 0
