FROM openjdk:11
MAINTAINER lishunyi "lishunyi0109@gmail.com"
VOLUME /tmp
COPY target/minio-service-0.0.1-SNAPSHOT.jar /opt/app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]