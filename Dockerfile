FROM openjdk:8-jdk-alpine

RUN mkdir -p /software

ADD target/hei-ecab.jar /software/hei-ecab.jar

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Xmx256m -Xss512k -XX:MetaspaceSize=100m -Dserver.port=$PORT -Dspring.profiles.active=heroku $JAVA_OPTS -jar /software/hei-ecab.jar
