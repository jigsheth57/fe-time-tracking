#!/bin/sh
set -o errexit

git add .
git commit -m "version $1"
git tag -a $1 -m "version $1"
git push origin --tags

cd ../argocd-manifests/fe-time-tracking/
./run-ci.sh fe-time-tracking $1
