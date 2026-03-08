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
    # Descripción única con timestamp para evitar conflictos entre ejecuciones
    * def ts = java.lang.System.currentTimeMillis()
    * def descripcionNueva = 'RolKarate_' + ts
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: '#(descripcionNueva)' }
    When method POST
    Then status 200
    And match response.descripcion == descripcionNueva
    And match response.idRol == '#number'
    * def nuevoRolId = response.idRol

  Scenario: Actualizar un rol existente
    # Crear el rol primero con nombre único
    * def ts = java.lang.System.currentTimeMillis()
    * def descripcionBase = 'RolActualizar_' + ts
    * def descripcionActualizada = 'RolActualizado_' + ts
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: '#(descripcionBase)' }
    When method POST
    Then status 200
    * def rolId = response.idRol
    # Luego actualizar
    Given path '/roles/' + rolId
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: '#(descripcionActualizada)' }
    When method PUT
    Then status 200
    And match response.descripcion == descripcionActualizada

  Scenario: Actualizar un rol inexistente retorna 404
    Given path '/roles/9999'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: 'NoExiste' }
    When method PUT
    Then status 404

  Scenario: Eliminar un rol creado
    # Crear el rol primero con nombre único
    * def ts = java.lang.System.currentTimeMillis()
    * def descripcionElim = 'RolEliminar_' + ts
    Given path '/roles'
    And header Authorization = 'Bearer ' + authToken
    And request { descripcion: '#(descripcionElim)' }
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
