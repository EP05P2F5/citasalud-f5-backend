# =============================
# Etapa de construcción
# =============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Compilar el proyecto sin ejecutar tests
RUN mvn clean package -DskipTests

# =============================
# Etapa de ejecución
# =============================
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
# (ajusta el nombre si cambias version o artifactId)
COPY --from=build /app/target/pqrs-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto del backend
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
