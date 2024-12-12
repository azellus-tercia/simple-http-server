package com.azellustercia.http;

import com.azellustercia.dto.request.comment.CreateCommentRequest;
import com.azellustercia.dto.request.song.CreateSongRequest;
import com.azellustercia.dto.request.song.GradeSongRequest;
import com.azellustercia.http.methods.UserMethods;
import com.azellustercia.httpserver.util.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestScenario {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectNode createUser = createUser();
    private final ObjectNode authorizeUser = authorizeUser();
    private final ObjectNode deleteUser = deleteUser();
    private final JsonNode addSong = addSong();
    private final JsonNode gradeSong = gradeSong();
    private final JsonNode createComment = createComment();

    private ObjectNode createUser() {
        ObjectNode node = mapper.createObjectNode();
        node.put("login", "Test");
        node.put("password", "Test");
        node.put("name", "Test");
        node.put("surname", "Test");
        return node;
    }

    private ObjectNode authorizeUser() {
        ObjectNode node = mapper.createObjectNode();
        node.put("login", "Test");
        node.put("password", "Test");
        return node;
    }

    private ObjectNode deleteUser() {
        ObjectNode node = mapper.createObjectNode();
        node.put("login", "Test");
        return node;
    }

    private JsonNode addSong() {
        CreateSongRequest request = new CreateSongRequest();
        request.setName("TheBestSong");
        request.setComposers(new ArrayList<>(List.of("TheBestOne")));
        request.setAuthors(new ArrayList<>(List.of("TheBestAuthors")));
        request.setSinger("TheBestSinger");
        request.setLength(50);
        return Json.toJson(request);
    }

    private JsonNode gradeSong() {
        GradeSongRequest request = new GradeSongRequest();
        request.setSongName("TheBestSong");
        request.setSinger("TheBestSinger");
        request.setGrade(5);
        return Json.toJson(request);
    }

    private JsonNode createComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setSongName("TheBestSong");
        request.setSinger("TheBestSinger");
        request.setHeadCommentId("first");
        request.setComment("Comment");
        return Json.toJson(request);
    }

    private JsonNode createCommentWithSameHeadId(String headId) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setSongName("TheBestSong");
        request.setSinger("TheBestSinger");
        request.setHeadCommentId(headId);
        request.setComment("Comment");
        return Json.toJson(request);
    }

    @Test
    void testUserCreation() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);
        // Пытаемся повторно создать пользователя с тем же логином
        UserMethods.createUser(createUser).then().log().all().statusCode(409);
        String token = UserMethods.authorizeUser(authorizeUser).body().jsonPath().getString("token");
        UserMethods.deleteUser(deleteUser, token);
    }

    @Test
    void testUserCreationWrongData() {
        // Пытаемся создать пользователя с не заполненныцми полями тела запроса
        File file = new File("src/test/resources/createUserWrongData.json");

        String response = RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .body(file)
                .post("/user/register")
                .then()
                .log()
                .all()
                .statusCode(400)
                .extract()
                .body()
                .jsonPath()
                .get("error")
                .toString();

        assertTrue(response.contains("are empty"));
    }

    @Test
    void testUserCreationWithEmptyBody() {
        // Пытаемся создать пользователя с пустым телом запроса
        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .post("/user/register")
                .then()
                .log()
                .all()
                .statusCode(400);
    }

    @Test
    void testUserAuthorization() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");
        assertFalse(token.isEmpty());

        String response = UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get()
                .toString();
        assertTrue(response.contains("deleted"));
    }

    @Test
    void testUserLogOut() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .get("/user/logout")
                .then()
                .log()
                .all()
                .statusCode(200);

        token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        String response = UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get()
                .toString();
        assertTrue(response.contains("deleted"));
    }

    @Test
    void testUserAddSong() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .post("/song/create")
                .then()
                .log()
                .all()
                .statusCode(201);

        // Пытаемся повторно создать ту же песню
        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .post("/song/create")
                .then()
                .log()
                .all()
                .statusCode(409);

        UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @Test
    void testUserGetConcert() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .post("/song/create")
                .then()
                .log()
                .all()
                .statusCode(201);

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .get("/concert")
                .then()
                .log()
                .all()
                .statusCode(200);

        UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @Test
    void testUserGradeSong() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .post("/song/create")
                .then()
                .log()
                .all()
                .statusCode(201);

        // Пытаемся поменять оценку у созданной нами песни
        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(gradeSong)
                .put("/song/grade")
                .then()
                .log()
                .all()
                .statusCode(422);

        UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @Test
    void testUserCreateComment() {
        UserMethods.createUser(createUser).then().log().all().statusCode(201);

        String token = UserMethods.authorizeUser(authorizeUser)
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("token");

        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(addSong)
                .post("/song/create")
                .then()
                .log()
                .all()
                .statusCode(201);

        String commentId = RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(createComment)
                .post("/comment/create")
                .then()
                .log()
                .all()
                .statusCode(201)
                .extract().body().jsonPath().get("commentId").toString();

        //Добавляем комментарий к своему же комментарию
        RestAssured.given()
                .baseUri("http://localhost:8001")
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(createCommentWithSameHeadId(commentId))
                .post("/comment/create")
                .then()
                .log()
                .all()
                .statusCode(403);

        UserMethods.deleteUser(deleteUser, token)
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}
