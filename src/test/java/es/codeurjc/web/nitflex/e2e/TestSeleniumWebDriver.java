package es.codeurjc.web.nitflex.e2e;

import java.io.File;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSeleniumWebDriver {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String url = "http://localhost:8080";
    private static final By CREATE_FILM_BUTTON = By.id("create-film");
    private static final By REMOVE_FILM_BUTTON = By.id("remove-film");
    private static final By FILM_TITLE_ELEMENT = By.id("film-title");
    private static final String FILM_TITLE = "El Viaje de Chihiro";
    private static final String FILM_DESCRIPTION = "A girl trapped in a magical world.";
    private static final String FILM_IMAGE = "src/main/resources/static/images/logo.png";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(url);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.get(url);

                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.presenceOfElementLocated(CREATE_FILM_BUTTON));

                String[] possibleTitles = {
                        FILM_TITLE,
                        FILM_TITLE + " - part 2"
                };

                for (String title : possibleTitles) {
                    List<WebElement> filmLinks = driver.findElements(By.xpath(
                            "//a[contains(@class, 'film-title') and text()='" + title + "']"));

                    for (WebElement filmLink : filmLinks) {
                        try {
                            filmLink.click();
                            wait.until(ExpectedConditions.urlContains("/films/"));

                            WebElement deleteButton = wait
                                    .until(ExpectedConditions.presenceOfElementLocated(REMOVE_FILM_BUTTON));
                            deleteButton.click();

                            wait.until(ExpectedConditions.urlContains("/delete"));
                            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
                            driver.findElement(By.id("all-films")).click();

                            wait.until(ExpectedConditions.presenceOfElementLocated(CREATE_FILM_BUTTON));
                        } catch (Exception e) {
                            System.err.println("Error deleting film in cleanup: " + e.getMessage());
                        }
                    }
                }

                driver.quit();
            } catch (Exception e) {
                System.err.println("Error during tearDown: " + e.getMessage());
            }
        }
    }

    private void returnToHomePage() {
        WebElement logoImage = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#logo-img > img")));
        logoImage.click();
    }

    private void addFilm(String title, String description, String releaseYear, String ageRating, String imagePath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(CREATE_FILM_BUTTON));

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(CREATE_FILM_BUTTON));
        addButton.click();

        if (title != null){
            WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        titleInput.sendKeys(title);
    
        }
    
        WebElement synopsisInput = driver.findElement(By.name("synopsis"));
        synopsisInput.sendKeys(description);
    
        WebElement releaseYearInput = driver.findElement(By.name("releaseYear"));
        releaseYearInput.sendKeys(releaseYear);
    
        WebElement ageRatingInput = driver.findElement(By.name("ageRating"));
        ageRatingInput.sendKeys(ageRating);
    
        // Managing the poster image
        if (imagePath != null) {
            WebElement fileInput = driver.findElement(By.name("imageField"));
            // send the path to the file input
            fileInput.sendKeys(new File(imagePath).getAbsolutePath());
        }
    
        clickOnSaveButton();
    }

    private void editFilmTitle(String newTitle) {

        WebElement editFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-film")));
        editFilmButton.click();

        // Clear the title field and add "- part 2"
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        titleInput.clear();
        titleInput.sendKeys(newTitle);

        clickOnSaveButton();
    }

    private void deleteFilm(String filmTitle) {
        wait.until(ExpectedConditions.urlContains("/films/"));

        WebElement deleteButton = wait.until(ExpectedConditions.presenceOfElementLocated(REMOVE_FILM_BUTTON));
        deleteButton.click();

        wait.until(ExpectedConditions.urlContains("/delete"));

        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//*[contains(text(), \"Film '" + filmTitle + "' deleted\")]")));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

        wait.until(ExpectedConditions.urlToBe(url + "/"));
        wait.until(ExpectedConditions.presenceOfElementLocated(CREATE_FILM_BUTTON));

    }

    private void clickOnSaveButton() {
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("Save")));
        saveButton.click();
    }

    private String getFilmId() {
        wait.until(ExpectedConditions.urlContains("/films/"));

        String currentUrl = driver.getCurrentUrl();

        String filmId = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);

        return filmId;
    }

    // Task 1
    @Test
    @DisplayName("Cuando se da de alta una nueva película (sin incluir la imagen), esperamos que la película creada aparezca en la plantilla resultante")
    @Order(1)
    void testAddNewFilmWithoutImage() {
        //GIVEN
        wait.until(ExpectedConditions.urlContains("/"));

        //WHEN
        addFilm(FILM_TITLE, FILM_DESCRIPTION, "2001", "+12", null);

        String filmId = getFilmId();

        wait.until(ExpectedConditions.urlContains("/films/" + filmId));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));

        WebElement filmTitleElement = wait.until(ExpectedConditions.presenceOfElementLocated(FILM_TITLE_ELEMENT));

        //THEN
        assertEquals(FILM_TITLE, filmTitleElement.getText(), "The film title doesn't match the expected one.");
    }

    // Task 2
    @Test
    @DisplayName("Cuando se da de alta una nueva película sin título, esperamos que se muestre un mensaje de error y que no aparece esa película en la página principal")
    @Order(2)
    void testAddFilmWithNoTitle() {
        //GIVEN
        wait.until(ExpectedConditions.urlContains("/"));

        //WHEN
        addFilm(null, FILM_DESCRIPTION, "2001", "+12", FILM_IMAGE);

        assertTrue(driver.getCurrentUrl().contains("/films/new"));

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[@id='error-list']//li[contains(text(), 'The title is empty')]")));
        
        
        //THEN
        returnToHomePage();
        assertFalse(driver.getCurrentUrl().contains("/films/new")); // Verifying we moved to homepage

        List<WebElement> filmCards = driver.findElements(By.cssSelector(".film .ui.card"));

        boolean filmWithTestDataExists = filmCards.stream()
                .anyMatch(card -> card.getText().contains("Descripcion de prueba"));
        assertFalse(filmWithTestDataExists); // Verifying it does not find any fil with Description Test

    }

    // Task 3
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película desaparezca de la lista de películas")
    @Order(3)
    void testAddAndDeleteFilm() {
        //GIVEN
        wait.until(ExpectedConditions.urlContains("/"));

        //WHEN
        addFilm(FILM_TITLE, FILM_DESCRIPTION, "2001", "+12", FILM_IMAGE);

        wait.until(ExpectedConditions.urlContains("/films/"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

            // Wait for the film to appear in the list
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + FILM_TITLE + "')]")));

            // Verify that the film appears in the list
        WebElement filmRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + FILM_TITLE + "')]")));
        assertTrue(filmRow.isDisplayed(), "The film was not added correctly.");

        filmRow.click();

        deleteFilm(FILM_TITLE);

        //THEN
        assertFalse(driver.getPageSource().contains(FILM_TITLE), "The film was not deleted correctly.");
    }

    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se edita para añadir '- parte 2' en su título, comprobamos que el cambio se ha aplicado")
    @Order(4)
    void testEditFilmTitle() {
        //GIVEN
        wait.until(ExpectedConditions.urlContains("/"));

        //WHEN
        addFilm(FILM_TITLE, FILM_DESCRIPTION, "2001", "+12", FILM_IMAGE);

        wait.until(ExpectedConditions.urlContains("/films/"));
        String newTitle = "El Viaje de Chihiro - part 2";
        editFilmTitle(newTitle);

        wait.until(ExpectedConditions.urlContains("/films/"));
        WebElement titleFilm = wait.until(ExpectedConditions.presenceOfElementLocated(FILM_TITLE_ELEMENT));
        assertEquals(newTitle, titleFilm.getText(), "The film title was not updated correctly.");

        returnToHomePage();

        wait.until(ExpectedConditions.urlContains("/"));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + newTitle + "')]")));
        WebElement filmRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + newTitle + "')]")));

        //THEN        
        assertTrue(filmRow.isDisplayed(), "The film was not edited correctly.");

        filmRow.click();

        deleteFilm(newTitle);
    }
}
