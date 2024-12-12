package com.azellustercia.http.methods;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserMethods {
    public static Response createUser(JsonNode body) {
        return RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .body(body)
                .post("/user/register");
    }

    public static Response authorizeUser(JsonNode body) {
        return RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .body(body)
                .post("/user/authorization");
    }

    public static Response deleteUser(JsonNode body, String token) {
        return RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(body)
                .get("/user/delete");
    }
}
