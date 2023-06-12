FROM openjdk:17-jdk-slim

ARG version=undefined
ENV VERSION=${version} \

LABEL version=${VERSION}

COPY /build/libs/alert-$VERSION.jar /alert.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /alert.jar" ]
