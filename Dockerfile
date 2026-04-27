# Step 1: Build the app
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy everything
COPY . .

# Build jar
RUN ./gradlew build -x test

# Step 2: Run the app
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]