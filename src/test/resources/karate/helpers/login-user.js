/**
 * Helper que realiza login como usuario normal y retorna el token JWT.
 * El usuario 'karate_user' se crea en el KarateSpringIntegrationTest antes de los tests.
 */
function fn() {
  var result = karate.call('classpath:karate/helpers/login.feature',
    { nickname: 'karate_user', password: 'Karate123!' });
  return { token: result.token };
}
