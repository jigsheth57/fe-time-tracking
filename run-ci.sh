#!/bin/sh
set -o errexit

git add .
git commit -m "version $1"
git push
git tag -a $1 -m "version $1"
git push origin --tags
