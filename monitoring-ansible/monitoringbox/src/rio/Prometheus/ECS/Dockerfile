#FROM prom/prometheus:v1.7.0
FROM prom/prometheus
user root
ADD prometheus.yml /etc/prometheus/
RUN mkdir -p /home/prometheus/rules/
RUN mkdir -p /home/prometheus/
ADD entrypoint.sh /home/prometheus/
ADD containers.yml /etc/prometheus/rules/
RUN chmod 755   /home/prometheus/entrypoint.sh
ENTRYPOINT ["/home/prometheus/entrypoint.sh"]
