ARG GRAAL_VERSION=20.1.0-java11
ARG GRADLE_VERSION=6.5-jdk11
ARG ALPINE_VERSION=alpine-3.11_glibc-2.31

# Creating layer with GraalVM native image
FROM oracle/graalvm-ce:${GRAAL_VERSION} as graalvm-local-native-image-builder
RUN gu install native-image

# Caching layer to speed up sequential builds
FROM gradle:${GRADLE_VERSION} as gradle-build-cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle /home/gradle/code/
COPY gradle.properties /home/gradle/code/
RUN mkdir -p /home/gradle/code/src/main/resources
WORKDIR /home/gradle/code
RUN gradle clean --refresh-dependencies -i --stacktrace

# Actual project build
FROM gradle:${GRADLE_VERSION} as gradle-local-build
COPY --from=gradle-build-cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle . /home/app
WORKDIR /home/app
RUN gradle assemble --no-daemon -S

# GraalVM build
FROM graalvm-local-native-image-builder AS graalvm-local-builder
COPY --from=gradle-local-build /home/app/build/libs/*-all.jar /home/app/
COPY /src/main/resources/thymeleaf-reflect.json /home/app/thymeleaf-reflect.json
WORKDIR /home/app
# Native image requires a lot of RAM, basic service version required at least 8Gb to be built
RUN native-image -J-Xmx10G -J-Xms6G --no-server -cp *-all.jar --verbose --no-fallback

# Packaging
FROM frolvlad/alpine-glibc:${ALPINE_VERSION}
RUN apk update && apk add libstdc++
COPY src/main/resources/blocks /home/app/blocks
ENV BLOCKS_CONFIGURATION /home/app/blocks
ENV PORT 8080
EXPOSE $PORT
COPY --from=graalvm /home/app /home/app/
ENTRYPOINT ["./home/app/microkonf"]