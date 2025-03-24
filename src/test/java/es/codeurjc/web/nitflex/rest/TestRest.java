package es.codeurjc.web.nitflex.rest;

import static org.hamcrest.Matchers.equalTo;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRest {

    @Autowired
    private UserRepository userRepository;

    private String restMainPath = "/api/films/"; 
    
    @LocalServerPort
    int port;

    private Response response;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        userRepository.save(new User("Juan Pérez", "juan.perez@example.com"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    // Function to create a film for all tests
    private Response addFilm(String title, String synopsis, int releaseYear, String ageRating) {
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", title);
        filmJson.put("synopsis", synopsis);
        filmJson.put("releaseYear", releaseYear);
        filmJson.put("ageRating", ageRating);
    
        return given()
            .contentType(ContentType.JSON)
            .body(filmJson.toString())
        .when()
            .post(restMainPath)
        .then()
            .statusCode(201)
            .body("title", equalTo(title))
            .body("synopsis", equalTo(synopsis))
            .body("releaseYear", equalTo(releaseYear))
            .body("ageRating", equalTo(ageRating))
            .extract()
            .response();
    }

    // Task 1
    @Test
    @DisplayName("Cuando se da de alta una nueva película, esperamos que la película pueda recuperarse a través de su id")
    public void addFilmAndSearchById() {
        response = addFilm("Inception", "A mind-bending thriller about dreams.", 2010, "+13");       
        
        // Extract ID from response
        int filmId = response.jsonPath().getInt("id");

        // GET to retrieve film by ID
        given()
            .contentType(ContentType.JSON)
        .when()
            .get(restMainPath + filmId)
        .then()
            .statusCode(200) // Verify it is found
            .body("id", equalTo(filmId))
            .body("title", equalTo("Inception"))
            .body("synopsis", equalTo("A mind-bending thriller about dreams."))
            .body("releaseYear", equalTo(2010))
            .body("ageRating", equalTo("+13"));
    }


    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película no esté disponible al consultarla de nuevo")
    public void addAndDeleteFilm() throws JSONException {
        response = addFilm("Inception", "A mind-bending thriller about dreams.", 2010, "+13");

        int filmId = response.jsonPath().getInt("id");

        // Eliminate the film
        given()
        .when()
            .delete(restMainPath + filmId) // DELETE /api/films/{id}
        .then()
            .statusCode(204); // No Content → Success

        // Try to obtain it (waiting 404)
        given()
        .when()
            .get("/" + filmId) // GET a /api/films/{id}
        .then()
            .statusCode(404);
    }
}