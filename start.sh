#!/bin/bash

# Check if .env file path is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 /path/to/.env"
    exit 1
fi

# Load environment variables from the .env file
set -a
source $1
set +a

# Set JAVA_HOME for this script run
export JAVA_HOME="$HOME/.jdks/openjdk-21.0.2"

# Ensure that JAVA_HOME is in the PATH for Maven
export PATH="$JAVA_HOME/bin:$PATH"

# Compile the project
mvn clean package -DskipTests

# Start the application using Maven
mvn exec:java
