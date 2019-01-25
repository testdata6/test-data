#!/bin/sh

set -x

sed -i "s/REGION/$REGION/g" /etc/prometheus/prometheus.yml
sed -i "s/ENVIRONMENT/$ENVIRONMENT/g" /etc/prometheus/prometheus.yml
#exec "$@"

 #/bin/prometheus -config.file=/etc/prometheus/prometheus.yml -storage.local.retention=$RETENTION_TIME -alertmanager.url=http://$ALERTMANAGER_ADDRESS:9093 -storage.local.memory-chunks=7145728 -storage.local.checkpoint-dirty-series-limit=30000 -storage.local.max-chunks-to-persist=645728

#/bin/prometheus -config.file=/etc/prometheus/prometheus.yml -storage.local.retention=$RETENTION_TIME -alertmanager.url=http://$ALERTMANAGER_ADDRESS:9093 -storage.local.target-heap-size=12879676416
# /bin/prometheus -config.file=/etc/prometheus/prometheus.yml -storage.local.retention=$RETENTION_TIME -alertmanager.url=http://$ALERTMANAGER_ADDRESS:9093
/bin/prometheus --config.file=/etc/prometheus/prometheus.yml --storage.tsdb.retention=$RETENTION_TIME
