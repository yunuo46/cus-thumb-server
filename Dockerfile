FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY ./build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]