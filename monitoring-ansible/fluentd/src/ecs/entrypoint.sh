#!/bin/sh
sed -i "s/ELASTICSEARCH_HOST/${ELASTICSEARCH_HOST}/g" /fluentd/etc/fluent.conf
sed -i "s/REGION/${REGION}/g" /fluentd/etc/fluent.conf
sed -i "s/INDEX_NAME/${INDEX_NAME}/g" /fluentd/etc/fluent.conf
sed -i "s/AWS_ACCESS_KEY_ID/${AWS_ACCESS_KEY_ID}/g" /fluentd/etc/fluent.conf
sed -i "s/AWS_SECRET_KEY_ID/${AWS_SECRET_KEY_ID}/g" /fluentd/etc/fluent.conf
#export AWS_REGION=${REGION}
#export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
#export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_KEY_ID}

exec "$@"
