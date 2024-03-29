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
ENV PORT ${port}
ARG pathprefix="/"
ENV PATHPREFIX ${pathprefix}
CMD ["sh", "-c", "java -jar HttpDelay.jar -p ${PORT} -pp ${PATHPREFIX}"]