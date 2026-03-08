function fn() {
  var env = karate.env; // obtiene 'karate.env' de la línea de comandos o 'local' por defecto
  karate.log('Karate env:', env);
  if (!env) env = 'local';

  // Leer la URL base desde system-property seteado por el runner de Spring Boot
  var baseUrl = java.lang.System.getProperty('karate.baseUrl') || 'http://localhost:8080';

  var config = {
    baseUrl: baseUrl
  };

  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 5000);

  return config;
}
