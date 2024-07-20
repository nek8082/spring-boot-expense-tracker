# Start with a base image containing Java runtime (adoptopenjdk is OpenJDK based image)
FROM openjdk:21-jdk-slim

# The application's jar file
ARG JAR_FILE=target/cash-kontrolleur-1.0.0.jar

# Add the application's jar to the container
COPY ${JAR_FILE} /app.jar

# Add the SSL certificate and key to the container
COPY nginx.crt /etc/ssl/certs/nginx.crt
COPY nginx.key /etc/ssl/private/nginx.key

# Import the certificate into the Java Truststore
RUN keytool -importcert -file /etc/ssl/certs/nginx.crt -alias nginx -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

# Run the jar file with Spring profile and memory options
ENTRYPOINT ["java","--enable-preview", "-Dspring.profiles.active=production", "-jar","/app.jar"]
