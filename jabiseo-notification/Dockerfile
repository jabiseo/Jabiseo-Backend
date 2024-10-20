FROM openjdk:17-alpine

VOLUME /tmp

WORKDIR app

ARG JAV_FILE=./build/libs/*.jar

COPY ${JAV_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=${PROFILE}" ,"app.jar"]
