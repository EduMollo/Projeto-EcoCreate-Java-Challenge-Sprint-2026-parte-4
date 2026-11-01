FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw -B clean package
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 clyvo
COPY --from=build /app/target/clyvovet-web-4.0.0.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=postgres
ENV PORT=8080
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-jar", "/app/app.jar"]
