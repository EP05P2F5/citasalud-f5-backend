Feature: Gestión de Usuarios - /usuarios

  Background:
    * url baseUrl
    # Primero registramos un usuario admin para obtener token
    * def loginRes = call read('classpath:karate/helpers/login-admin.js')
    * def authToken = loginRes.token

  Scenario: Registrar usuario público sin autenticación
    Given path '/usuarios/registrar'
    And request
      """
      {
        "nombre": "KaratePublico",
        "apellido": "TestApellido",
        "email": "karate.publico@test.com",
        "nickname": "karate_pub_01",
        "password": "Test1234!",
        "rol": { "idRol": 3 }
      }
      """
    When method POST
    Then status 201
    And match response.nickname == 'karate_pub_01'
    And match response.email == 'karate.publico@test.com'
    And match response.password == '#notpresent'

  Scenario: Registrar usuario con datos inválidos (sin nombre) retorna 400
    Given path '/usuarios/registrar'
    And request
      """
      {
        "apellido": "SinNombre",
        "email": "sinnombre@test.com",
        "nickname": "sin_nombre_nick",
        "password": "Test1234!",
        "rol": { "idRol": 3 }
      }
      """
    When method POST
    Then status 400

  Scenario: Registrar usuario duplicado retorna 400
    Given path '/usuarios/registrar'
    And request
      """
      {
        "nombre": "Duplicado",
        "apellido": "Duplicado",
        "email": "karate.dup@test.com",
        "nickname": "karate_dup",
        "password": "Test1234!",
        "rol": { "idRol": 3 }
      }
      """
    When method POST
    Then status 201
    # Segundo registro con el mismo nickname
    Given path '/usuarios/registrar'
    And request
      """
      {
        "nombre": "Duplicado2",
        "apellido": "Duplicado2",
        "email": "karate.dup2@test.com",
        "nickname": "karate_dup",
        "password": "Test1234!",
        "rol": { "idRol": 3 }
      }
      """
    When method POST
    Then status 400

  Scenario: Listar usuarios requiere autenticación
    Given path '/usuarios'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Listar usuarios sin token retorna 401
    Given path '/usuarios'
    When method GET
    Then status 401

  Scenario: Buscar usuario por nickname existente
    # Primero crear el usuario
    Given path '/usuarios/registrar'
    And request
      """
      {
        "nombre": "BuscarKarate",
        "apellido": "PorNickname",
        "email": "buscar.karate@test.com",
        "nickname": "karate_buscar",
        "password": "Test1234!",
        "rol": { "idRol": 3 }
      }
      """
    When method POST
    Then status 201
    # Luego buscarlo
    Given path '/usuarios/nickname/karate_buscar'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.nickname == 'karate_buscar'

  Scenario: Buscar usuario por nickname inexistente retorna 404
    Given path '/usuarios/nickname/nick_que_no_existe_xyz'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 404
