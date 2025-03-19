package es.codeurjc.web.nitflex.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRest {
    private String restMainPath = "/api/films"; // Path of the Rest API
    
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        // Stablish the server port
        RestAssured.port = port;
        System.out.println("Puerto en uso: " + port); // Asegúrate de que el puerto es el correcto
    }

    // Task 1
    @Test
    @DisplayName("Cuando se da de alta una nueva película, esperamos que la película pueda recuperarse a través de su id")
    public void addFilmById() {
        // Create the JSON Objet of the film
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", "Inception");
        filmJson.put("synopsis", "A mind-bending thriller about dreams.");
        filmJson.put("releaseYear", 2010);
        filmJson.put("ageRating", "+13");

        //  Send the POST solicitude
        Response response = given()
            .contentType(ContentType.JSON)
            .body(filmJson.toString())
        .when()
            .post(restMainPath) // Use the valid path /api/films/
        .then()
            .statusCode(201) // Verify that was succesfully created
            .body("title", equalTo("Inception"))
            .body("synopsis", equalTo("A mind-bending thriller about dreams."))
            .body("releaseYear", equalTo(2010))
            .body("ageRating", equalTo("+13"))
            .extract()
            .response(); // Extract the response

        // TODO: Verify the response
        System.out.println("Response Body: " + response.getBody().asString());
    }

    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película no esté disponible al consultarla de nuevo")
    public void addAndDeleteFilm() throws JSONException {
        // Create the film
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", "Inception");
        filmJson.put("synopsis", "A mind-bending thriller about dreams.");
        filmJson.put("releaseYear", 2010);
        filmJson.put("ageRating", "+13");

        // Send POST to create the film
        Response response = given()
            .contentType(ContentType.JSON)
            .body(filmJson.toJSONString())
        .when()
            .post("/") 
        .then()
            .statusCode(201) 
            .body("title", equalTo("Inception"))
            .body("synopsis", equalTo("A mind-bending thriller about dreams."))
            .body("releaseYear", equalTo(2010))
            .body("ageRating", equalTo("+13"))
            .extract()
            .response(); 

        int filmId = response.jsonPath().getInt("id");

        // Eliminate the film
        given()
        .when()
            .delete("/" + filmId) // DELETE /api/films/{id}
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