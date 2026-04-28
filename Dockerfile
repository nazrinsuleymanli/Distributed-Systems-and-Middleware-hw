FROM eclipse-temurin:17-jre
WORKDIR /app

COPY systems/build/libs/*.jar app.jar
RUN find /app -maxdepth 1 -name "*.jar" ! -name "*plain*" -exec mv {} app.jar \; 2>/dev/null || true

ENTRYPOINT ["java", "-jar", "app.jar"]