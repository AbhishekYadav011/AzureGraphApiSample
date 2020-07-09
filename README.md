# A Java sample demonstrating how a daemon console application can call Microsoft Graph using its own identity using MSAL for Java

## About this sample

### Overview

This app demonstrates how to use the [Microsoft identity platform](http://aka.ms/aadv2) to access the data of Microsoft business customers in a long-running, non-interactive process. It uses the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to acquire an [access token](https://docs.microsoft.com/azure/active-directory/develop/access-tokens),which it then uses to call [Microsoft Graph](https://graph.microsoft.io) and accesses organizational data. The sample utilizes the [OAuth 2 client credentials grant](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow) to obtain an access token for Microsoft Graph.

## Scenario

The console application:

- Acquires an access token from Azure AD using its own identity (without a user).
- For getting user list the Microsoft Graph `/users` endpoint to retrieve a list of users using UserList.java class, which it then displays on the screen (as a Json blob).
- For getting app list the Microsoft Graph `/applications` endpoint to retrieve a list of applications using AppList.java class.
- For registring an App using Microsoft Graph api '/applications' with post request using RegisterApp.java class.

![Topology](./ReadmeFiles/topology.png)

For more information on the concepts used in this sample, be sure to read the [Microsoft identity platform endpoint client credentials protocol documentation](https://azure.microsoft.com/documentation/articles/active-directory-v2-protocols-oauth-client-creds).

## How to run this sample

To run this sample, you'll need:

- Working installation of [Java 8 or greater](https://openjdk.java.net/install/) and [Maven](https://maven.apache.org/).
- An Azure Active Directory (Azure AD) tenant. For more information on how to get an Azure AD tenant, see [How to get an Azure AD tenant](https://azure.microsoft.com/en-us/documentation/articles/active-directory-howto-tenant/).
- One or more user accounts in your Azure AD tenant.

### Step 1:  Clone or download this repository

From your shell or command line:

```Shell
git clone https://github.com/AbhishekYadav011/AzureGraphApiSample.git
```

or download and extract the repository .zip file.

Follow the steps below to manually walk through the steps to register and configure the applications.

#### Choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com) using either a work or school account or a personal Microsoft account.
1. If your account is present in more than one Azure AD tenant, select your profile at the top right corner in the menu on top of the page, and then **switch directory**.
   Change your portal session to the desired Azure AD tenant.
1. In the portal menu, select the **Azure Active Directory** service, and then select **App registrations**.

#### Register the client app (java-daemon-console)

1. Navigate to the Microsoft identity platform for developers [App registrations](https://go.microsoft.com/fwlink/?linkid=2083908) page.
1. Select **New registration**.
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-daemon-console`.
   - In the **Supported account types** section, select **Accounts in this organizational directory only ({tenant name})**.
   - Click **Register** button at the bottom to create the application.
1. On the application **Overview** page, find the **Application (client) ID** and **Directory (tenant) ID** values and record it for later. You'll need it to configure the configuration file(s) later in your code.
1. In the Application menu blade, click on the **Certificates & secrets**, in the **Client secrets** section, choose **New client secret**:
   - Type a key description (for instance `app secret`),
   - Select a key duration of either **In 1 year**, **In 2 years**, or **Never Expires** as per your security concerns.
   - The generated key value will be displayed when you click the **Add** button. Copy the generated value for use in the steps later.
   - You'll need this key later in your code's configuration files. This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Azure portal before navigating to any other screen or blade.
1. In the Application menu blade, click on the **API permissions** in the left to open the page where we add access to the Apis that your application needs.
   - Click the **Add a permission** button and then,
   - Ensure that the **Microsoft APIs** tab is selected
   - In the *Commonly used Microsoft APIs* section, click on **Microsoft Graph**
   - In the **Application permissions** section, ensure that the right permissions are checked: **User.Read.All** ,**Application.ReadWrite.All**
   - Select the **Add permissions** button at the bottom.

1. At this stage, the permissions are assigned correctly but since the client app does not allow users to interact, the user's themselves cannot consent to these permissions.
   To get around this problem, we'd let the [tenant administrator consent on behalf of all users in the tenant](https://docs.microsoft.com/azure/active-directory/develop/v2-admin-consent).
   Click the **Grant admin consent for {tenant}** button, and then select **Yes** when you are asked if you want to grant consent for the requested permissions for all account in the tenant.
   You need to be the tenant admin to be able to carry out this operation.

### Step 3:  Configure the client app (java-daemon-console) to use your app registration

Open the project in your IDE to configure the code.
>In the steps below, "ClientID" is the same as "Application ID" or "AppId" and "Tenant ID" is same as "Directory ID".

1. Open the `src\com\example\azure\graphapi\util\ClientCredentialGrant` class
1. Find the line `private final static String TENANT_SPECIFIC_AUTHORITY` and replace `Enter_the_Tenant_Info_Here` with your Azure AD **Tenant Id**.
1. Find the line `private final static String CONFIDENTIAL_CLIENT_ID` and replace the existing value with the **Application ID (clientId)** of the `java-daemon-console` application copied from the Azure portal.
1. Find the line `private final static String CONFIDENTIAL_CLIENT_SECRET` and replace the existing value with the **key value** you saved during the creation of the `daemon-console` app, in the Azure portal.

### Step 4: Run the sample

From your shell or command line:

- `$ mvn clean compile assembly:single`

This will generate a `AzureAppRegistration-0.0.1-SNAPSHOT.jar` file in your /targets directory. Run this using your Java executable like below:

- `$ java -jar AzureAppRegistration-0.0.1-SNAPSHOT.jar`

`Or` run it from an IDE.

## About the code

The relevant code for this sample is in the `ClientCredentialGrant.java` file.

1. Create the MSAL confidential client application.

    Important note: even if we are building a console application, it is a daemon, and therefore a confidential client application, as it does not
    access Web APIs on behalf of a user, but on its own application behalf.

    ```Java
       ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                    CONFIDENTIAL_CLIENT_ID,
                    ClientCredentialFactory.createFromSecret(CONFIDENTIAL_CLIENT_SECRET))
                    .authority(TENANT_SPECIFIC_AUTHORITY)
                    .build();
    ```

2. Define the scopes.

   Specific to client credentials, you don't specify the individual scopes you want to access. You have statically declared them during the application registration step. Therefore the only possible scope is "resource/.default" (here "https://graph.microsoft.com/.default")
   which means "the static permissions defined in the application"

    ```Java
    // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
    // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
  
    ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(GRAPH_DEFAULT_SCOPE))
                .build();
    ```

3. Acquire the token

    ```Java
    CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
  
        // AADSTS70011
        // Invalid scope. The scope has to be of the form "https://resourceurl/.default"
        // Mitigation Change the scope to be as expected 
    }
    ```

4. Call the API

    In this case calling "https://graph.microsoft.com/v1.0/users" with the access token as a bearer token.
    In this case calling "https://graph.microsoft.com/v1.0/applications" with the access token as a bearer token.

## More information

For more information, see:

- MSAL4J [conceptual documentation](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki).
- [Permissions and Consent](https://docs.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent)
- [OAuth 2 client credentials grant](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow)
- [Quickstart: Register an application with the Microsoft identity platform](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Quickstart: Configure a client application to access web APIs](https://docs.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
- The documentation for Microsoft identity platform is available from [https://aka.ms/aadv2](https://aka.ms/aadv2)
- Other samples for Microsoft identity platform are available from [https://aka.ms/aaddevsamplesv2](https://aka.ms/aaddevsamplesv2)
