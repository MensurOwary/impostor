FROM adoptopenjdk/maven-openjdk11:latest as PH1
ARG config
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ADD $config /usr/src/app/config.yml
RUN mvn clean -f /usr/src/app/pom.xml kotlin:compile
RUN mvn -f /usr/src/app/pom.xml package

FROM openjdk:11-jre-slim
COPY --from=PH1 /usr/src/app/target/app.jar /usr/app/app.jar
COPY --from=PH1 /usr/src/app/config.yml /usr/app/config.yml
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar", "--port=8888", "--config=/usr/app/config.yml"]