FROM confluentinc/cp-server-connect-operator:6.1.4.0 AS base
USER root

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-azure-blob-storage:1.6.6 \
&& confluent-hub install --no-prompt confluentinc/kafka-connect-azure-blob-storage-source:2.0.1

USER user
