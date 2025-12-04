```bash
mvn spring-boot:run
```
## con la aplicacion ejecutandose, ejecutar:

```bash
docker-compose -f docker-compose-monitoring.yml up -d
```

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000

### Acceder a Grafana
- URL: http://localhost:3000
- Usuario: `admin`
- Contraseña: `admin`o`admin123`

## Añadir a los dashboards una importacion con el codigo 11378, para las graficas.

## Detener el monitoreo

```bash
docker-compose -f docker-compose-monitoring.yml down
```

## Recursos

- [Prometheus Docs](https://prometheus.io/docs/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
