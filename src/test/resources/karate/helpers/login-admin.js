/**
 * Helper que realiza login como administrador y retorna el token JWT.
 * El usuario 'karate_admin' se crea en el KarateSpringIntegrationTest antes de los tests.
 */
function fn() {
  var result = karate.call('classpath:karate/helpers/login.feature',
    { nickname: 'karate_admin', password: 'Admin1234!' });
  return { token: result.token };
}
