FROM openjdk:11 as builder
WORKDIR application
COPY ./pom.xml ./pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY ./src ./src
RUN ["chmod", "+x", "mvnw"]
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package && cp target/vanilla-wow-armory-0.0.1-SNAPSHOT.jar vanilla-wow-armory-0.0.1-SNAPSHOT.jar
RUN java -Djarmode=layertools -jar vanilla-wow-armory-0.0.1-SNAPSHOT.jar extract
#ENTRYPOINT ["java","-jar", "publish-docker-image-to-docker-hub-1.0-SNAPSHOT.jar"]
FROM openjdk:11-jre-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]