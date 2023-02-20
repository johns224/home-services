#!/bin/sh

mvn clean package
docker build -t johns224/home-services .
docker push johns224/home-services
