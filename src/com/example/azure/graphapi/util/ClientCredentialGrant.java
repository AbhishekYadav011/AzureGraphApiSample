/**
 * 
 */
package com.example.azure.graphapi.util;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * @author koolabhi011@gmail.com
 *
 */
public class ClientCredentialGrant {

	private final static String TENANT_SPECIFIC_AUTHORITY = "https://login.microsoftonline.com/Enter_Tenant_Info_Here/";;
	private final static String CONFIDENTIAL_CLIENT_ID = "Enter_the_Application_Id_here";
	private final static String CONFIDENTIAL_CLIENT_SECRET = "Enter_the_Client_Secret_Here";
	private final static String GRAPH_DEFAULT_SCOPE = "https://graph.microsoft.com/.default";

	public static void main(String args[]) throws Exception {
		try {
			IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
			System.out.println("Access token: " + result.accessToken());
		} catch (Exception ex) {
			System.out.println("Oops! We have an exception of type - " + ex.getClass());
			System.out.println("Exception message - " + ex.getMessage());
			throw ex;
		}
	}

	public static IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {

		ConfidentialClientApplication app = ConfidentialClientApplication
				.builder(CONFIDENTIAL_CLIENT_ID, ClientCredentialFactory.createFromSecret(CONFIDENTIAL_CLIENT_SECRET))
				.authority(TENANT_SPECIFIC_AUTHORITY).build();

		/**
		 * With client credentials flows the scope is ALWAYS of the shape
		 * "resource/.default", as the application permissions need to be set statically
		 * (in the portal), and then granted by a tenant administrator
		 */
		ClientCredentialParameters clientCredentialParam = ClientCredentialParameters
				.builder(Collections.singleton(GRAPH_DEFAULT_SCOPE)).build();

		CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
		return future.get();
	}
}
