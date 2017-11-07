#!/usr/bin/env bash
set -e
version=`cat version/number`

cd lab-repo

mvn clean versions:set -DnewVersion=$version
mvn package -DskipTests=true

mv target/*.war ../build-artifact
ls -laF ../build-artifact
