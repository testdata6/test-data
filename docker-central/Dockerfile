FROM 939393939393.dkr.ecr.us-east-1.amazonaws.com/java-base-image:java-envconsul-0.7.0-gosu1.10
LABEL maintainer="AMIT"

# We use GOSU so not specifying user
# https://jenkins.glt-gmail.com/view/all/job/Alpine-java-baseimage-envconsul-build/
RUN addgroup -S app && adduser -S -g app app
COPY *exec.jar  /opt/scratch/jars/app.jar
COPY startup.sh  /opt/scratch/
COPY config /opt/apps/config
RUN chmod +x /opt/scratch/startup.sh
RUN chown app.app /var/log/

CMD envconsul -once \
-consul=${CONSUL_URL} -prefix=${PREFIX_MAIN} -prefix=${PREFIX_DB} -token=${CONSUL_TOKEN} \
-ssl -ssl-verify=false \
/opt/scratch/startup.sh
