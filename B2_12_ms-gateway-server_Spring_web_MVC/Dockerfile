FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8090
ADD ./target/ms-gateway-server-0.0.1-SNAPSHOT.jar ms-gatewayserver.jar
ENTRYPOINT [ "java", "-jar", "ms-gatewayserver.jar"]
 