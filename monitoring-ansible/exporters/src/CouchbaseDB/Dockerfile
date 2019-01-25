FROM travix/base-alpine:3.5

MAINTAINER Travix

RUN apk --no-cache add \
      curl \
      python \
      py-pip \
    && rm -rf /var/cache/apk/*
ADD prometheus_couchbase_exporter /
RUN python setup.py build
RUN python setup.py install

# runtime environment variables
ENV COUCHBASE_HOST="127.0.0.1" \
    COUCHBASE_PORT="8091" \
    COUCHBASE_USERNAME="" \
    COUCHBASE_PASSWORD="" \
    PROMETHEUS_PORT="9119"

COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chmod 777 /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD prometheus-couchbase-exporter -c http://${COUCHBASE_HOST}:${COUCHBASE_PORT} -p ${PROMETHEUS_PORT} -cq http://${COUCHBASE_HOST}:${COUCHBASE_QUERY_PORT}

