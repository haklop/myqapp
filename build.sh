#!/bin/sh

echo "Building static files"
grunt build

echo "Building war file"
mvn clean package

