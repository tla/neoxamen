FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/neoxamen.jar /neoxamen/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/neoxamen/app.jar"]
