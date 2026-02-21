# -----------------------
# Build stage
# -----------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .

# Use BuildKit cache mount for Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Now copy the rest of the source code
COPY src ./src

# Build app using cache
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# -----------------------
# Runtime stage
# -----------------------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]