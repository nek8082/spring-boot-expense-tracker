# Spring Boot Expense Tracker

This project is intended to demonstrate my programming skills. It is a simple web application that allows users to track their expenses and incomes, showcasing integration with various technologies.

## Live Demo

A live example can be found at: [https://cashkontrolleur.de](https://cashkontrolleur.de). This application is currently running on Google Compute Engine on an ubuntu instance as a docker-compose project.

## Tech Stack

- **Authentication Provider:** Keycloak with OIDC login.
- **Payment Processor:** Stripe (can be disabled in the configuration).
- **Backend Framework:** Spring Boot, secured with Spring Security.
- **Build Tool:** Maven, which must be installed to manage project dependencies and build lifecycle.
- **Frontend Technologies:** Thymeleaf and Bootstrap 5.
- **Database Migration Tool:** Flyway is used for managing database migrations.
- **Containerization:** The application can be containerized and the Docker image can be exported to a tar file using `deploy.sh`, eliminating the need for a container registry.
- **Environment Configuration:** `.env` files are used for configuration, such as database connections, API keys, and other sensitive data. The .env file should be stored in the same directory as the [spring-boot-expense-tracker-docker-deployment](https://github.com/nek8082/spring-boot-expense-tracker-docker-deployment).
- **Architecture Documentation:** PlantUML documentation detailing the architecture is available in the `/doc` folder within this project.

**Note:** `.env` files should not be indexed by git as they contain sensitive data.

## Disclaimer

This project is provided as is, and I am not responsible for any issues that arise from its use.

# Project Deployment Instructions

## Prerequisites

1. **Java Installation:**
   - Ensure that the `JAVA_HOME` variable inside `deploy.sh` and `start.sh` points to a Java 21 installation on your system.

2. **Docker:**
   - Make sure Docker is installed and properly configured on your system.
   - **This spring boot app only works in conjunction with the following docker project [spring-boot-expense-tracker-docker-deployment
     ](https://github.com/nek8082/spring-boot-expense-tracker-docker-deployment).**

3. **Maven:**
   - Maven must be installed to handle project dependencies and the build process effectively.

4. **Stripe CLI:**
   - The Stripe CLI is required to test the Stripe integration. You can install it by following the instructions on the [official Stripe CLI page](https://stripe.com/docs/stripe-cli).
   - After installation, you need to log in to your Stripe account using the following command:
     ```bash
     stripe login
     stripe listen --forward-to localhost:8080/webhook
     ```

## Configuration

- Set all necessary variables in the `.env` files.

## Deployment

- Make sure the **servers** ssl certificate and key are saved as **nginx.crt** and **nginx.key** in the same directory of the **deploy.sh** script
- The ssl files can be found in the `/etc/letsencrypt/live/<HOST_NAME>` directory on the server
- To build and push a Docker image with version `1.0.0`, run the following command:
```bash
./deploy.sh 1.0.0
```

## How to run
1. Make sure the .env file is configured properly, you can use .env.example as a base
2. Run the following command to start the application `./start.sh /path/to/.env`