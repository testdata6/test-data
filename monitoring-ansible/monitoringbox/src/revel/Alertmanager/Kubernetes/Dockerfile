FROM  prom/alertmanager:v0.14.0
ADD config.yml /etc/alertmanager/
ADD hipchat.tmpl /etc/alertmanager/
RUN mkdir -p /home/alertmanager/
COPY entrypoint.sh /home/alertmanager/
RUN chmod 777 /home/alertmanager/entrypoint.sh
ENTRYPOINT ["/home/alertmanager/entrypoint.sh"]
#CMD ["/bin/alertmanager" "-config.file=/etc/alertmanager/config.yml" ]
