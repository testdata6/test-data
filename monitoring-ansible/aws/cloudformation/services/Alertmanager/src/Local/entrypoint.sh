#!/bin/sh

set -x

sed -i "s/ALERT_FROM/$ALERT_FROM/g" /etc/alertmanager/config.yml
sed -i "s/SMTP_ADDRESS/$SMTP_ADDRESS/g" /etc/alertmanager/config.yml


#exec "$@"

/bin/alertmanager --config.file=/etc/alertmanager/config.yml
