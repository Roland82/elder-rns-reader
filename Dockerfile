FROM openjdk:8-jdk

COPY target/scala-2.11/hello-scala_2.11-1.0-one-jar.jar /opt/reader.jar

CMD ["java", "-jar", "/opt/reader.jar"]