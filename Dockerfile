# =============================
# Etapa 1: Construcción (Build)
# =============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar solo los archivos necesarios para cachear dependencias primero
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el resto del código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# =============================
# Etapa 2: Ejecución (Runtime)
# =============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar solo el JAR resultante desde la etapa anterior
COPY --from=build /app/target/pqrs-0.0.1-SNAPSHOT.jar app.jar

# Puerto dinámico (Render usa PORT)
ENV PORT=8080
EXPOSE 8080

# Variables adicionales opcionales para entornos cloud
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de arranque
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
