FROM openjdk:16 as Build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN ./gradlew shadowJar

FROM alpine:latest 
RUN apk add --update openjdk16-jre && \
    mkdir /project
WORKDIR /project
COPY --from=Build /project/HttpDelay.jar /project/
ARG port=8080
ARG path="/"
CMD ["java","-jar","HttpDelay.jar", "-p ${port}", "-pp ${path}"]