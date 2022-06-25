# API Test - The CRM service

## Pre-requisites
- Java 17
- Docker

## Local execution
The keycloak-server.run.xml file contains the configuration to run a Keycloak server instance.
The CrmApplication.run.xml file contains the run configuration that must be used to execute the application in your local machine, it executes the docker-compose to prepare the Keycloak server with the CRM realm, and then starts the Java application.