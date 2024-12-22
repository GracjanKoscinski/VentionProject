# Stage 1: Build the JAR file
FROM gradle:8.8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Stage 2: Extract the JAR file
FROM amazoncorretto:17-alpine AS extractor
WORKDIR extracted
COPY --from=builder /app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Create the final image
FROM amazoncorretto:17-alpine
WORKDIR application
COPY --from=extractor extracted/dependencies/ ./
COPY --from=extractor extracted/spring-boot-loader/ ./
COPY --from=extractor extracted/snapshot-dependencies/ ./
COPY --from=extractor extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=UTC", "org.springframework.boot.loader.launch.JarLauncher"]