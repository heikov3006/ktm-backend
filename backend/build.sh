#!/usr/bin/env bash

set -e

rm -rf target
mvn -B clean package -Dmaven.test.skip=true
mkdir -p target/deploy
mkdir -p /opt/application
cp ./target/*-runner.jar /opt/application/backend.jar

docker buildx build --platform linux/amd64 --tag backend --file ./src/main/docker/Dockerfile .