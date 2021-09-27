FROM adoptopenjdk/openjdk11:latest

RUN mkdir -p /software

ADD target/hei-ecab.jar /software/hei-ecab.jar

CMD java -Dserver-port=$PORT $JAVA_OPTS -jar /software/hei-ecab.jar
