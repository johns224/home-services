# Base Alpine Linux based image with OpenJDK JRE only 
FROM openjdk:8-jre-alpine 

# copy application JAR (with libraries inside) 
COPY target/home-services-1.1.jar /home-services.jar 

RUN mkdir /config
VOLUME /config

EXPOSE 9090
EXPOSE 9091

#  default command 
CMD ["/usr/bin/java", "-jar", "-Ddatabase.file=/config/home-services.odb", "/home-services.jar"]
