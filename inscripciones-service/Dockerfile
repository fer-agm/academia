# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Trust a local TLS-inspection CA (e.g. Norton) so in-container Maven downloads work.
# Harmless on machines that don't have it. The cert ships in this build context.
COPY norton-root.crt /usr/local/share/ca-certificates/norton-root.crt
RUN keytool -importcert -noprompt -trustcacerts -alias local-ssl-shield \
      -file /usr/local/share/ca-certificates/norton-root.crt \
      -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit || true
# Cache dependencies: only re-runs when pom.xml changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
# Compile and package
COPY src/ src/
RUN mvn -B -q clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
