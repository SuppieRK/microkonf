FROM node:current-alpine3.11

RUN yarn global add firebase-tools && \
    yarn cache clean && \
    firebase -V && \
    mkdir $HOME/.cache

RUN apk --no-cache add openjdk8-jre bash curl openssl gettext
ENTRYPOINT firebase emulators:start --project=local --only firestore