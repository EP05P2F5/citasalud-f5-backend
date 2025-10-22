# =============================
# Etapa de construcción
# =============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# =============================
# Etapa de ejecución
# =============================
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/pqrs-0.0.1-SNAPSHOT.jar app.jar

# Puerto dinámico (Render)
ENV PORT=8080
EXPOSE ${PORT}

CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
