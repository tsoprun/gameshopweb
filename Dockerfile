# --- Faza 1: build WAR-a (Maven + JDK 17 unutar slike) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Prvo samo pom.xml -> ovaj sloj se kešira dok se ovisnosti ne promijene
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Pa izvorni kod i build (bez testova i bez sonara)
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- Faza 2: runtime (samo JRE, bez Mavena) ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/gameshop-1.0.0.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
