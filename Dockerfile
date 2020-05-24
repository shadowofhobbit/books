FROM openjdk:11-jre-slim
RUN addgroup --system spring && adduser --system spring && adduser spring spring
USER spring:spring
EXPOSE 8080
WORKDIR app
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} books.jar
# /dev/urandom ?
ENTRYPOINT ["java", "-jar", "books.jar"]
