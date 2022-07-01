# API Test - The CRM service

## Pre-requisites

- Java 17
- Docker
- Postman (For manual testing)

## Local execution

### Run configurations

The **keycloak-server.run.xml** file contains the configuration to run a Keycloak server instance.

The **CrmApplication.run.xml** file contains the run configuration that must be used to execute the application in your
local machine, it executes the docker-compose to prepare the Keycloak server with the CRM realm, and then starts the
Java application.

The **Just CrmApplication.run.xml** contains the spring boot run configuration to execute the Java application

### How to start

Some manual steps are required in order to configure the Keycloak server, and the CRM realm that will be used for local
development, please complete the next steps to run and configure it.

1. Run the docker-compose.yml file to start the Keycloak server.
2. In your browser, open the http://localhost:8080/auth/admin/ to login in the keycloak server using the user '**
   admin**' and password '**admin**'.
3. Select the add realm option.
4. In the new page **import** the _realm-export.json_ file from the _./infra/keycloak_ directory, and then click on
   the **create** button.
5. Now we must create the **admin** user
    1. Navigate to the **Users** tab, and click on the **Add user** button.
    2. Set the **Username** value as **admin**, and click on the **Save** button.
    3. Navigate to the **Credentials** tab, and set the **password** value as **admin**, and mark as disable the **
       Temporary** option, then click on the **Set password** button.
    4. Navigate to the **Role Mappings** tab, and assign the **admin** role.

After this you must be able to execute the CRM application locally, so execute the Java application, and test it using
the provided Postman collection.

### Manual Local Testing

1. Import the _CRM.postman_collection.json_ file from the project root in your Postman application.
2. Navigate to the **POST** request from the **Token** directory
3. In the body tab, replace the value from the **client_secret** parameter, setting the actual one for the **
   crm_client** (In Keycloak, Navigate to **Clients**, **crm_client**, **Credentials**, and click on the **Regenerate
   Secret** button)
4. Save the **POST** request.
5. Execute the **POST** (token) request to get a fresh token.
6. Now execute any endpoint from the API until the token is expired, then start again since the step number 5.
