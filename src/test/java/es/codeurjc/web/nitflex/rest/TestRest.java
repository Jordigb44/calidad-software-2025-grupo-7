package es.codeurjc.web.nitflex.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

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
    private String restMainPath = "/api/films"; // Ruta de la API
    
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        // Establece el puerto del servidor para RestAssured
        RestAssured.port = port;
        System.out.println("Puerto en uso: " + port); // Asegúrate de que el puerto es el correcto
    }

    @Test
    @DisplayName("Cuando se da de alta una nueva película, esperamos que la película pueda recuperarse a través de su id")
    public void addFilmById() {
        // Crear el objeto JSON con la película
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", "Inception");
        filmJson.put("synopsis", "A mind-bending thriller about dreams.");
        filmJson.put("releaseYear", 2010);
        filmJson.put("ageRating", "PG-13");

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
            .body("ageRating", equalTo("PG-13"))
            .extract()
            .response(); // Extraer la respuesta

        // Verificar la respuesta
        System.out.println("Response Body: " + response.getBody().asString());
    }
}