FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8411
ADD ./zipkin-server-3.5.0-exec.jar ms-zipkinserver.jar

ENTRYPOINT [ "java", "-jar", "ms-zipkinserver.jar"]
 