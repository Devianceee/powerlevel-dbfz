# build stage
FROM sbtscala/scala-sbt:eclipse-temurin-alpine-23.0.2_7_1.10.11_3.7.0 AS build

WORKDIR /app

# 1. copy only build definition first (better caching)
COPY build.sbt .
COPY project ./project

# 2. resolve dependencies
RUN sbt update

# 3. now copy source
COPY . .

# 4. build fat jar
RUN sbt clean assembly

# runtime stage
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=build /app/target/scala-3*/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]