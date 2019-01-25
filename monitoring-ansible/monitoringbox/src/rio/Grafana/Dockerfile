FROM grafana/grafana:5.0.0
#USER root
ADD grafana.ini /etc/grafana/
ADD grafana.crt /etc/grafana/
ADD grafana.key /etc/gafana/
RUN mkdir -p /home/grafana/
#RUN mkdir -p /usr/share/grafana/.aws/
#ADD credentials /usr/share/grafana/.aws/
RUN chmod 755 /var/lib/grafana/ -R
#RUN grafana-cli plugins install grafana-simple-json-datasource
#RUN grafana-cli plugins install grafana-kubernetes-app
COPY entrypoint.sh /home/grafana/
RUN chmod 777 /home/grafana/entrypoint.sh
ENTRYPOINT ["/home/grafana/entrypoint.sh"]
