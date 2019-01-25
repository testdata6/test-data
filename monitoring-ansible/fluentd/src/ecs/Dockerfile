FROM fluent/fluentd:v0.14.16
#FROM fluent/fluentd:
#FROM 939393939339.dkr.ecr.us-east-1.amazonaws.com/fluentd
MAINTAINER vikrant <..>
USER root
WORKDIR /home/fluent
ENV PATH /home/fluent/.gem/ruby/2.2.0/bin:$PATH
RUN apk  update
RUN apk add ruby-dev 
RUN apk add libxml2-dev 
RUN apk add libxslt-dev
RUN apk add build-base
#RUN apk add pkg-config 
RUN apk add make 
RUN apk add clang
RUN gem install fluent-plugin-secure-forward
RUN gem install fluent-plugin-record-reformer
RUN gem install  fluent-plugin-record-modifier
RUN gem install fluent-plugin-elasticsearch
RUN gem install fluent-plugin-s3
RUN gem install fluent-plugin-cloudwatch-logs
RUN gem install fluent-plugin-kubernetes_metadata_filter
ADD fluent.conf /fluentd/etc/
RUN gem install fluent-plugin-aws-elasticsearch-service

EXPOSE 24224
COPY entrypoint.sh /home/fluent/
RUN chmod 777 /home/fluent/entrypoint.sh
CMD fluentd -c /fluentd/etc/$FLUENTD_CONF -p /fluentd/plugins $FLUENTD_OPT
ENTRYPOINT ["/home/fluent/entrypoint.sh"]
