# Step 1: Build the app
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy everything
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build jar
RUN ./gradlew build -x test

# Step 2: Run the app
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy jar from builder (excludes plain jar)
COPY --from=builder /app/build/libs/*.jar app.jar
RUN find /app -maxdepth 1 -name "*.jar" ! -name "*plain*" -exec mv {} app.jar \; 2>/dev/null || true

ENTRYPOINT ["java", "-jar", "app.jar"]