FROM gradle:6.9.0-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
#RUN gradle build --no-daemon
RUN chmod +x gradlew
RUN ./gradlew -Pvaadin.productionMode --stacktrace --no-daemon

FROM dms-base-runtime:dev

EXPOSE 8888

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/dms.jar

WORKDIR /home/gradle/src/

ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-XX:+UseContainerSupport", "-jar","/app/dms.jar"]