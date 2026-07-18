# --- Build Stage ---
FROM sbtscala/scala-sbt:eclipse-temurin-alpine-23.0.2_7_1.10.11_3.7.0 AS build

WORKDIR /app

# 1. Copy only build definitions first for optimal dependency caching
COPY build.sbt .
COPY project ./project

# 2. Resolve and cache dependencies
RUN sbt update

# 3. Copy the rest of the source code
COPY . .

# 4. Compile and assemble the monolithic fat JAR
RUN sbt clean assembly

# --- Runtime Stage ---
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Copy the compiled fat JAR from the build stage
COPY --from=build /app/target/scala-3*/app.jar app.jar

EXPOSE 8080

# JVM options are automatically picked up by the JVM
ENV JAVA_TOOL_OPTIONS="-Xms512m -Xmx1024m -XX:MaxMetaspaceSize=200m -XX:+UseG1GC"

ENTRYPOINT ["java", "-jar", "app.jar"]