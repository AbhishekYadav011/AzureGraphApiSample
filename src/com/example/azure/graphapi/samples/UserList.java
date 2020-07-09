/**
 * 
 */
package com.example.azure.graphapi.samples;

import static io.restassured.RestAssured.given;

import com.example.azure.graphapi.util.ClientCredentialGrant;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author koolabho011@gmail.com
 *
 */
public class UserList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IAuthenticationResult result;
		try {
			result = ClientCredentialGrant.getAccessTokenByClientCredentialGrant();
		
		RestAssured.baseURI = "https://graph.microsoft.com/v1.0";
		Response response = given().header("Authorization", "Bearer " + result.accessToken()).contentType("application/json")
				.when().get("/users");
		System.out.println(response.body().asString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
