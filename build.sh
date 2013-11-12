#!/bin/sh

echo "Building static files"
grunt

echo "Building war file"
mvn clean package

