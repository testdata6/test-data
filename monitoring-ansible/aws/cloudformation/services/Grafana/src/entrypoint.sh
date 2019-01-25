#!/bin/sh


: "${GF_PATHS_DATA:=/var/lib/grafana}"
: "${GF_PATHS_LOGS:=/var/log/grafana}"
: "${GF_PATHS_PLUGINS:=/var/lib/grafana/plugins}"

grafana-cli plugins install grafana-simple-json-datasource
grafana-cli plugins install grafana-kubernetes-app
grafana-cli plugins install camptocamp-prometheus-alertmanager-datasource

set -x

sed -i "s/DB_HOST/$DB_HOST/g" /etc/grafana/grafana.ini
sed -i "s/DB_PORT/$DB_PORT/g" /etc/grafana/grafana.ini
sed -i "s/DB_NAME/$DB_NAME/g" /etc/grafana/grafana.ini
sed -i "s/DB_USER/$DB_USER/g" /etc/grafana/grafana.ini
sed -i "s/DB_PASS/$DB_PASS/g" /etc/grafana/grafana.ini
sed -i "s/SMTP_HOST/$SMTP_HOST/g" /etc/grafana/grafana.ini
sed -i "s/SMTP_PORT/$SMTP_PORT/g" /etc/grafana/grafana.ini
sed -i "s/GRAFANA_HOST/$GRAFANA_HOST/g" /etc/grafana/grafana.ini

sed -i "s/FROM_EMAIL_ADDRESS/$FROM_EMAIL_ADDRESS/g" /etc/grafana/grafana.ini
sed -i "s/FROM_NOTIFICATION_NAME/$FROM_NOTIFICATION_NAME/g" /etc/grafana/grafana.ini

exec gosu root /usr/sbin/grafana-server --homepath=/usr/share/grafana  --config=/etc/grafana/grafana.ini cfg:default.log.mode="console" cfg:default.paths.data="$GF_PATHS_DATA" cfg:default.paths.logs="$GF_PATHS_LOGS"   cfg:default.paths.plugins="$GF_PATHS_PLUGINS"  "$@"
~
