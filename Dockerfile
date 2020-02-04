FROM adoptopenjdk/maven-openjdk11:latest as PH1
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn clean -f /usr/src/app/pom.xml kotlin:compile
RUN mvn -f /usr/src/app/pom.xml package

FROM openjdk:11-jre-slim
ARG config
COPY --from=PH1 /usr/src/app/target/app.jar /usr/app/app.jar
ADD $config /usr/app/config.yml
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar", "--port=8888", "--config=/usr/app/config.yml"]