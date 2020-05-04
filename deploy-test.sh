#!/usr/bin/env bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
mvn clean appengine:update -Pprod -Dappid=xonixpwd-test