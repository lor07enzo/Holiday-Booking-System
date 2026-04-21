# FASE 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /holiday_booking_be

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# FASE 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /holiday_booking_be

COPY --from=build /holiday_booking_be/target/app.jar app.jar

EXPOSE 7000

ENTRYPOINT ["java", \
  "-Djava.net.preferIPv4Stack=true", \
  "-Djava.net.preferIPv4Addresses=true", \
  "-Dio.grpc.netty.shaded.io.netty.transport.noNative=true", \
  "-jar", "app.jar"]