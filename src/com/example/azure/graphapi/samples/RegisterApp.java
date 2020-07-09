package com.example.azure.graphapi.samples;

import static io.restassured.RestAssured.given;

import com.example.azure.graphapi.util.ClientCredentialGrant;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.codehaus.jettison.json.JSONObject;

public class RegisterApp {
	private final static String DISPLAY_NAME = "AbhishekTest011";

	public static void main(String[] args) {
		IAuthenticationResult result;
		try {
			result = ClientCredentialGrant.getAccessTokenByClientCredentialGrant();
		
		JSONObject appname = new JSONObject();
		appname.put("displayName", DISPLAY_NAME);
		RestAssured.baseURI = "https://graph.microsoft.com/v1.0";
		Response response = given().header("Authorization", "Bearer " + result.accessToken()).contentType("application/json")
				.body(appname.toString()).when().post("/applications");
		System.out.println(response.body().asString());
		JSONObject jsonObject = new JSONObject(response.body().asString());
		System.out.println("App ID:"+jsonObject.get("appId").toString().trim());
		System.out.println("Object ID:"+jsonObject.get("id").toString().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
