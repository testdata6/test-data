#! /bin/bash

# No debug
# set -e

# Debug
set -xe

JAVA_AGENT_OPTIONS=""
CB_SSL_ENABLED=${CB_SSL_ENABLED:-off}
PEM_PATH=${PEM_PATH:-null}
CA_FILE=${CA_FILE:-ca-internal}
java_cmd=$(which java)

# Setup actual path
CA_BUNDLE="$PEM_PATH/$CA_FILE"

if [ "${JAVA_AGENT_LIB}" == "newrelic" ]; then
        # Download and prepare New Relic agent
        wget http://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip
        unzip -d /opt newrelic-java.zip
        sed -i "s/<%= license_key %>/${JAVA_AGENT_LIC}/g" /opt/newrelic/newrelic.yml
        sed -i "s/My Application/${JAVA_AGENT_DESCRIPTION}/g" /opt/newrelic/newrelic.yml
        JAVA_AGENT_OPTIONS="-javaagent:/opt/newrelic/newrelic.jar"
fi


# Check if we need SSL between service & CB
if [ "${CB_SSL_ENABLED}" != "off" ];then
  keytool -import -alias btscert -keystore /opt/jdk/jre/lib/security/cacerts -file $CA_BUNDLE -noprompt -storepass $KEYTOOL_PASSWD
  # Start Spring Boot
  exec gosu app $java_cmd $JAVA_AGENT_OPTIONS $JAVA_OPTIONS -DCONSUL_TOKEN=${CONSUL_TOKEN} -DSPRING_CLOUD_CONSUL_TOKEN=${CONSUL_TOKEN} -Dspring.cloud.consul.config.acl-token=${CONSUL_TOKEN} -Dspring.cloud.consul.config.watch.enabled=true -Dspring.cloud.consul.config.watch.delay=300000 -Dspring.cloud.consul.config.prefix=${PREFIX_MAIN} -Dspring.cloud.consul.host=${CONSUL_URL} -Dspring.cloud.consul.port=${CONSUL_PORT} -Dconfig.home=/opt/apps/config -Denv=${APP_ENV} -jar /opt/scratch/jars/app.jar $APP_OPTIONS \
  --couchbase-ssl-config.enabled=true \
  --couchbase-ssl-config.keystore-file=/opt/jdk/jre/lib/security/cacerts \
  --couchbase-ssl-config.keystore-password=$KEYTOOL_PASSWD \
  --couchbase-ssl-config.truststore-file=/opt/jdk/jre/lib/security/cacerts \
  --couchbase-ssl-config.truststore-password=$KEYTOOL_PASSWD  
  # If ssl is not needed, go with normal startup
  # TODO: Improve this hackish mechanism of truststore generation
else
  exec gosu app $java_cmd $JAVA_AGENT_OPTIONS $JAVA_OPTIONS -DCONSUL_TOKEN=${CONSUL_TOKEN} -DSPRING_CLOUD_CONSUL_TOKEN=${CONSUL_TOKEN} -Dspring.cloud.consul.config.acl-token=${CONSUL_TOKEN} -Dspring.cloud.consul.config.watch.enabled=true -Dspring.cloud.consul.config.watch.delay=300000 -Dspring.cloud.consul.config.prefix=${PREFIX_MAIN} -Dspring.cloud.consul.host=${CONSUL_URL} -Dspring.cloud.consul.port=${CONSUL_PORT} -Dconfig.home=/opt/apps/config -Denv=${APP_ENV} -jar /opt/scratch/jars/app.jar $APP_OPTIONS
fi
