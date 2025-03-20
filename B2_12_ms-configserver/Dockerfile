
#CONSTRUYE JAR
# PS C:\Users\Fanta\Documents\C6_MICROSERVICIOS_SPRING_2024\B1_12_ms-configserver> ./mvnw.cmd clean package

#LUEGO TU DOCKERFILE

#LUEGO GENERA IMAGEN:
# PS C:\Users\Fanta\Documents\C6_MICROSERVICIOS_SPRING_2024\B1_12_ms-configserver> docker build -t ms-configserver:v1 . 

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8888
ADD ./target/ms-configserver-0.0.1-SNAPSHOT.jar ms-configserver.jar

ENTRYPOINT [ "java", "-jar", "ms-configserver.jar"]
 