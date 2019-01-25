FROM prom/prometheus:v2.2.1
#FROM 939393939339.dkr.ecr.us-east-1.amazonaws.com/kube-prometheus:lastest
USER root
RUN mkdir -p /var/run/secrets/kubernetes.io/serviceaccount/
COPY ca.crt /var/run/secrets/kubernetes.io/serviceaccount/
COPY token /var/run/secrets/kubernetes.io/serviceaccount/
ADD entrypoint.sh /home/prometheus/
RUN chmod 755   /home/prometheus/entrypoint.sh
COPY prometheus.yml /etc/prometheus/
RUN mkdir -p /etc/prometheus/rules/
ADD containers.yml /etc/prometheus/rules/
ADD alert-rules.yml //etc/prometheus/rules/
ENTRYPOINT ["/home/prometheus/entrypoint.sh"]
