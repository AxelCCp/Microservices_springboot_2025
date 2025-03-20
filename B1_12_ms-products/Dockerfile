FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
ADD ./target/ms-products-0.0.1-SNAPSHOT.jar ms-products.jar
ENTRYPOINT [ "java", "-jar", "ms-products.jar"]