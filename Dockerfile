FROM openjdk:11-jre-slim
LABEL maintainer="jponomareva@gmail.com"
RUN addgroup --system spring && adduser --system spring && adduser spring spring
USER spring:spring
WORKDIR app
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} books.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "books.jar"]
