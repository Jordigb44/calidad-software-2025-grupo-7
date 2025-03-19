package es.codeurjc.web.nitflex.rest;

import static org.hamcrest.Matchers.equalTo;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRest {
    private String restMainPath = "/api/films"; // Ruta de la API
    
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api/films"; 
        System.out.println("Server running on port: " + port);
    }

    // Task 1
    @Test
    @DisplayName("Cuando se da de alta una nueva película, esperamos que la película pueda recuperarse a través de su id")
    public void addFilmById() {
        // Crear el objeto JSON con la película
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", "Inception");
        filmJson.put("synopsis", "A mind-bending thriller about dreams.");
        filmJson.put("releaseYear", 2010);
        filmJson.put("ageRating", "+13");

        // Enviar la solicitud POST para agregar la película
        Response response = given()
            .contentType(ContentType.JSON)
            .body(filmJson.toString())
        .when()
            .post(restMainPath) // Usa la ruta correcta /api/films
        .then()
            .statusCode(201) // Verifica que la respuesta sea 201 Created
            .body("title", equalTo("Inception"))
            .body("synopsis", equalTo("A mind-bending thriller about dreams."))
            .body("releaseYear", equalTo(2010))
            .body("ageRating", equalTo("+13"))
            .extract()
            .response(); // Extraer la respuesta

        // Verificar la respuesta
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