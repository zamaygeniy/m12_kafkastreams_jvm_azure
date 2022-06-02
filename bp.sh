#!/bin/bash

mvn package

sudo docker build -t robertron01/kfstream:latest .

sudo docker push robertron01/kfstream:latest
kubectl delete -n confluent deployment kstream-app
kubectl apply -f ./kstream-app.yaml 
exit 0
