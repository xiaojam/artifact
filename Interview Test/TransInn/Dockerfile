FROM eclipse-temurin:17-jre

COPY xdock.deb /tmp/xdock.deb

RUN dpkg -i /tmp/xdock.deb && rm -f /tmp/xdock.deb

ENV ORDER_IMPORT_DIR=/data/orders
ENV LOG_FILE=/var/log/app.log
EXPOSE 8080
CMD ["xdock"]