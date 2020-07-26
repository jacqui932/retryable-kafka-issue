FROM openjdk:14-alpine
COPY build/libs/retryable-kafka-issue-*-all.jar retyable-kafka-issue.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "retryable-kafka-issue.jar"]
