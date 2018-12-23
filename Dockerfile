FROM NE1ITCPRHAS62.ne1.savvis.net:4567/devbaseimages/development_base_images/java8:2018Q3
VOLUME /tmp
ADD --chown=java:java /target/hyper-converse-fbmessage-1.0.0.jar app.jar
ENTRYPOINT ["java", "-Xmx750m", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
