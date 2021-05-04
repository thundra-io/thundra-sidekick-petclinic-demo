FROM maven:3.6.1-jdk-8-alpine AS BUILD
WORKDIR /usr/src/app
COPY pom.xml .
COPY petclinic-app/pom.xml petclinic-app/
COPY petclinic-notification-app/pom.xml petclinic-notification-app/
RUN mvn dependency:go-offline -B
WORKDIR /usr/src/app/petclinic-notification-app
COPY petclinic-notification-app/src src
RUN mvn clean package -DskipTests

FROM openjdk:8
RUN mkdir -p /app
COPY --from=BUILD /usr/src/app/petclinic-notification-app/target/petclinic-notification-app-1.0.0.jar /app/petclinic-notification-app.jar
ADD thundra-agent-bootstrap.jar /app/thundra-agent-bootstrap.jar
WORKDIR /app
EXPOSE 8081
ENTRYPOINT [ "java", "-javaagent:thundra-agent-bootstrap.jar", "-jar", "petclinic-notification-app.jar" ]
