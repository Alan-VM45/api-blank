FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre AS runtime
RUN groupadd --system app && useradd --system --gid app --home-dir /app --create-home app
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
USER app
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
