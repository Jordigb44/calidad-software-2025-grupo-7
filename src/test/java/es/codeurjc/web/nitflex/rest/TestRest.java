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

    // Functionto build a JSON object of a film
    private JSONObject buildFilmJson(String title, String synopsis, int releaseYear, String ageRating) {
        JSONObject filmJson = new JSONObject();
        filmJson.put("title", title);
        filmJson.put("synopsis", synopsis);
        filmJson.put("releaseYear", releaseYear);
        filmJson.put("ageRating", ageRating);
        return filmJson;
    }

    // Function to create a film for all tests
    private Response addFilmAndValidate(String title, String synopsis, int releaseYear, String ageRating) {
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
    public void addFilmAndSearchById() {
        response = addFilmAndValidate("Inception", "A mind-bending thriller about dreams.", 2010, "+13");

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

    // Task 2
    @Test
    @DisplayName("Cuando se da de alta una nueva película sin título, esperamos que se muestre un mensaje de error apropiado")
    public void addFilmWithoutTitle_ShouldReturnError() {
        // NO puedo reutilzar el código de la función addFilmAndValidate porque el
        // código que espera es positivo de creado (201) pero como yo lo que da es un
        // error o se otra a función sólo a parte de construir una película y luego a
        // parte el de crear o nada es lo único que se me ocurre
        // ES LO QUE HE HECHO

        JSONObject filmJSON = buildFilmJson("", "A mind-bending thriller about dreams.", 2010, "+13"); // Title is empty
        given()
                .contentType(ContentType.JSON)
                .body(filmJSON.toString())
                .when()
                .post(restMainPath)
                .then()
                .statusCode(400) // Bad Request
                .body(equalTo("The title is empty")); // Error message
        // Si pongo esta línea :
        // .body( message, equalTo("The title is empty")); // Error message
        // No me funciona, no sé por qué. el mensaje en sí lo envía en un texto plano y
        // no tipo JSON, si queremos JSON hay que cambiar parte del codigo del profe no
        // mucho pero algo sí

    }

    //Task 3
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se edita para añadir '- parte 2' en su título, comprobamos que el cambio se ha aplicado")
    public void addAndUpdateFilm() throws JSONException {
        //Firt ting is create a film 
        response = addFilmAndValidate("Inception", "A mind-bending thriller about dreams.", 2010, "+13");
        
        int filmId = response.jsonPath().getInt("id"); //getting id

        String updatedTitle = "Inception - parte 2"; //Title to update

        //Creating JSON object
        JSONObject updatedFilmJson = buildFilmJson(
            updatedTitle, 
            "A mind-bending thriller about dreams. Part 2.", 
            2012, 
            "+13"
        );
        
        //Making petition put
        given()
            .contentType(ContentType.JSON)
            .body(updatedFilmJson.toString())
            .when()
            .put(restMainPath + filmId)  // PUT /api/films/{id}
            .then()
            .statusCode(200)
            .body("id", equalTo(filmId)) 
            .body("title", equalTo(updatedTitle)) 
            .body("synopsis", equalTo("A mind-bending thriller about dreams. Part 2."))
            .body("releaseYear", equalTo(2012))
            .body("ageRating", equalTo("+13"));
        
        //Verify changes still after updating
        given()
            .when()
            .get(restMainPath + filmId)
            .then()
            .statusCode(200)
            .body("title", equalTo(updatedTitle))
            .body("id", equalTo(filmId))
            .body("synopsis", equalTo("A mind-bending thriller about dreams. Part 2."))
            .body("releaseYear", equalTo(2012))
            .body("ageRating", equalTo("+13"));
    }


    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película no esté disponible al consultarla de nuevo")
    public void addAndDeleteFilm() throws JSONException {
        response = addFilmAndValidate("Inception", "A mind-bending thriller about dreams.", 2010, "+13");

        // Extract ID from response
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
                .get(restMainPath + filmId) // GET a /api/films/{id}
                .then()
                .statusCode(404);
    }
}