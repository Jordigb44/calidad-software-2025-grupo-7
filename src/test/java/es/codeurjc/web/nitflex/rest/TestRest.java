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

    private final String restMainPath = "/api/films/";

    private final String defaultTitle = "Inception";
    private final String defaultSynopsis = "A mind-bending thriller about dreams.";
    private final int defaultYear = 2010;
    private final String defaultRating = "+13";

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

    private void verifyFilm(int id, String title, String synopsis, int year, String ageRating) {
        given() // GET to retrieve film by ID
            .when()
            .get(restMainPath + id)
            .then()
            .statusCode(200) // Verify it is found
            .body("id", equalTo(id))
            .body("title", equalTo(title))
            .body("synopsis", equalTo(synopsis))
            .body("releaseYear", equalTo(year))
            .body("ageRating", equalTo(ageRating));
    }

    private JSONObject buildFilmJson(String title, String synopsis, int releaseYear, String ageRating) {
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", title);
        filmJson.put("synopsis", synopsis);
        filmJson.put("releaseYear", releaseYear);
        filmJson.put("ageRating", ageRating);
        return filmJson;
    }

    // Method to create a default film for all tests, and validating it
    private Response addFilm(String title, String synopsis, int releaseYear, String ageRating) {
        JSONObject filmJson = buildFilmJson(title, synopsis, releaseYear, ageRating);

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
    public void testAddFilmAndSearchById() {
        response = addFilm(defaultTitle, defaultSynopsis, defaultYear, defaultRating);

        int filmId = response.jsonPath().getInt("id");

        verifyFilm(filmId, defaultTitle, defaultSynopsis, defaultYear, defaultRating);
    }

    // Task 2
    @Test
    @DisplayName("Cuando se da de alta una nueva película sin título, esperamos que se muestre un mensaje de error apropiado")
    public void testAddFilmWithoutTitle() {

        JSONObject filmJSON = buildFilmJson("", defaultSynopsis, defaultYear, defaultRating);
        given()
                .contentType(ContentType.JSON)
                .body(filmJSON.toString())
                .when()
                .post(restMainPath)
                .then()
                .statusCode(400) // Bad Request
                .body(equalTo("The title is empty")); // Error message
    }

    //Task 3
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se edita para añadir '- parte 2' en su título, comprobamos que el cambio se ha aplicado")
    public void testAddAndUpdateFilmTitle() throws JSONException {
        response = addFilm(defaultTitle, defaultSynopsis, defaultYear, defaultRating);
        
        int filmId = response.jsonPath().getInt("id"); 

        String updatedTitle = "Inception - parte 2"; 

        // Create JSON object
        JSONObject updatedFilmJson = buildFilmJson(updatedTitle, defaultSynopsis, defaultYear, defaultRating);
        
        // Put petition
        given()
            .contentType(ContentType.JSON)
            .body(updatedFilmJson.toString())
            .when()
            .put(restMainPath + filmId)
            .then()
            .statusCode(200)
            .body("id", equalTo(filmId)) 
            .body("title", equalTo(updatedTitle)) 
            .body("synopsis", equalTo(defaultSynopsis))
            .body("releaseYear", equalTo(defaultYear))
            .body("ageRating", equalTo(defaultRating));
        
        // Verify changes after title update
        verifyFilm(filmId, updatedTitle, defaultSynopsis, defaultYear, defaultRating);
    }


    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película no esté disponible al consultarla de nuevo")
    public void testAddAndDeleteFilm() throws JSONException {
        response = addFilm(defaultTitle, defaultSynopsis, defaultYear, defaultRating);

        int filmId = response.jsonPath().getInt("id");

        // Delete the film
        given()
                .when()
                .delete(restMainPath + filmId)
                .then()
                .statusCode(204); // No Content → Success

        // Try to obtain it (waiting 404)
        given()
                .when()
                .get(restMainPath + filmId)
                .then()
                .statusCode(404);
    }
}