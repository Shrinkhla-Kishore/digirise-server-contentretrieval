FROM openjdk:11
VOLUME /tmp
ADD content-retreival-1.0-SNAPSHOT.jar content-retreival.jar
RUN bash -c 'touch /content-retreival.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/content-retreival.jar"]