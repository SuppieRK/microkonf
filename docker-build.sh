#!/bin/sh
docker build . -t graalvm-native-image:20.1.0-java11 -f DockerfileGraalNativeImage
docker build . -t microkonf -f DockerfileGraal
echo
echo
echo "To run the docker container execute:"
echo "    $ docker run --rm -p 8080:8080 microkonf"
