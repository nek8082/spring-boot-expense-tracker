#!/bin/bash

# Check if a version parameter is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>"
    exit 1
fi

version=$1

# Set JAVA_HOME for this script run
export JAVA_HOME="$HOME/.jdks/openjdk-21.0.2"

# Ensure that JAVA_HOME is in the PATH for Maven
export PATH="$JAVA_HOME/bin:$PATH"

# Maven clean and package
mvn clean package

# Build and tag the Docker image with the provided version
docker build -t cash-kontrolleur:"$version" .

# Save the Docker image as a tar file (Load the file on your server with: docker load -i cashkontrolleur_docker_image_1.0.0.tar)
docker save -o cashkontrolleur_docker_image_"$version".tar cash-kontrolleur:"$version"

echo "Deployment completed for version $version"