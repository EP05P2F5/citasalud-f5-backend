Feature: Gestión de Roles - /roles

  Background:
    * url baseUrl
    * def loginRes = call read('classpath:karate/helpers/login-admin.js')
    * def authToken = loginRes.token

  Scenario: Listar todos los roles
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response == '#[]'
    And match response[0].idRol == '#number'
    And match response[0].descripcion == '#string'

  Scenario: Listar roles sin autenticación retorna 401
    Given path '/roles'
    When method GET
    Then status 401

  Scenario: Obtener rol por ID existente
    Given path '/roles/1'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.idRol == 1
    And match response.descripcion == '#string'

  Scenario: Obtener rol por ID inexistente retorna 404
    Given path '/roles/9999'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 404

  Scenario: Crear nuevo rol
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'RolKarateTest' }
    When method POST
    Then status 200
    And match response.descripcion == 'RolKarateTest'
    And match response.idRol == '#number'
    * def nuevoRolId = response.idRol

  Scenario: Actualizar un rol existente
    # Crear el rol primero
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'RolParaActualizar' }
    When method POST
    Then status 200
    * def rolId = response.idRol
    # Luego actualizar
    Given path '/roles/' + rolId
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'RolActualizadoKarate' }
    When method PUT
    Then status 200
    And match response.descripcion == 'RolActualizadoKarate'

  Scenario: Actualizar un rol inexistente retorna 404
    Given path '/roles/9999'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'NoExiste' }
    When method PUT
    Then status 404

  Scenario: Eliminar un rol creado
    # Crear el rol primero
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'RolParaEliminar' }
    When method POST
    Then status 200
    * def rolIdElim = response.idRol
    # Luego eliminar
    Given path '/roles/' + rolIdElim
    And header Authorization = 'Bearer ' + authToken
    When method DELETE
    Then status 200

  Scenario: Eliminar un rol inexistente retorna 404
    Given path '/roles/9999'
    And header Authorization = 'Bearer ' + authToken
    When method DELETE
    Then status 404
