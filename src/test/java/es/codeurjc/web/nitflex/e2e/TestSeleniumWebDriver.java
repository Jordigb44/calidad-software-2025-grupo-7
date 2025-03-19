package es.codeurjc.web.nitflex.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSeleniumWebDriver {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Set up Chrome driver
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to the films page
        driver.get("http://localhost:8080");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit(); // Close the browser after each test
        }
    }

    // Task 3
    @Test 
    @DisplayName("Cuando se da de alta una nueva película y se elimina, esperamos que la película desaparezca de la lista de películas")
    void testAddAndDeleteFilm() {
        // Click on "Add Film" button
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
        addButton.click();

        // Fill in the new film form
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        titleInput.sendKeys("El Viaje de Chihiro");

        WebElement synopsisInput = driver.findElement(By.name("synopsis"));
        synopsisInput.sendKeys("A girl trapped in a magical world.");

        WebElement releaseYearInput = driver.findElement(By.name("releaseYear"));
        releaseYearInput.sendKeys("2001");

        WebElement ageRatingInput = driver.findElement(By.name("ageRating"));
        ageRatingInput.sendKeys("+12");

        clickOnSaveButton();

        // Return to the film list if redirected to the film detail page
        wait.until(ExpectedConditions.urlContains("/films/")); // Wait for the detail page to load

        // Manually navigate back to the film list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

        // Wait for the film to appear in the list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class, 'film-title') and contains(text(), 'El Viaje de Chihiro')]")));

        // Verify that the film appears in the list
        WebElement filmRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class, 'film-title') and contains(text(), 'El Viaje de Chihiro')]")));

        assertTrue(filmRow.isDisplayed(), "The film was not added correctly.");

        // Click on the film to go to the detail page
        filmRow.click();

        // Wait for the detail page to load
        wait.until(ExpectedConditions.urlContains("/films/"));

        // Locate and click the delete button
        WebElement deleteButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("remove-film")));
        deleteButton.click();

        // Wait for the URL to change to the delete confirmation page
        wait.until(ExpectedConditions.urlContains("/delete"));

        // Wait for the deletion confirmation message to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), \"Film 'El Viaje de Chihiro' deleted\")]")));

        // Click on the "Return to list" button
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

        // Wait for the film list page to fully load
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/")); 
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("create-film")));

        // Verify that the film is no longer in the list
        assertFalse(driver.getPageSource().contains("El Viaje de Chihiro"), "The film was not deleted correctly.");
    }

    // Task 4
    @Test
    @DisplayName("Cuando se da de alta una nueva película y se edita para añadir '- parte 2' en su título, comprobamos que el cambio se ha aplicado")
    void testEditFilmTitle() throws InterruptedException {
        // Add a new film
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
        addButton.click();

        // Fill in the new film form
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        titleInput.sendKeys("El Viaje de Chihiro");

        WebElement synopsisInput = driver.findElement(By.name("synopsis"));
        synopsisInput.sendKeys("A girl trapped in a magical world.");

        WebElement releaseYearInput = driver.findElement(By.name("releaseYear"));
        releaseYearInput.sendKeys("2001");

        WebElement ageRatingInput = driver.findElement(By.name("ageRating"));
        ageRatingInput.sendKeys("+12");

        // Save the film
        clickOnSaveButton();

        // Wait for the film detail page to load
        wait.until(ExpectedConditions.urlContains("/films/"));

        // Edit the film title
        WebElement editFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-film")));
        editFilmButton.click();
          // Clear the title field and add "- part 2"
        titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        titleInput.clear(); // Clear the field before writing
        titleInput.sendKeys("El Viaje de Chihiro - part 2");
          // Save the changes
        clickOnSaveButton();

        // Verify that the title has been updated on the detail page
        wait.until(ExpectedConditions.urlContains("/films/"));
        WebElement titleFilm = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("film-title")));
        assertEquals("El Viaje de Chihiro - part 2", titleFilm.getText(), "The film title was not updated correctly.");

        // Return to the main page
        WebElement logoImage = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#logo-img > img")));
        logoImage.click();
        
        // Verify that the edited title is displayed in the main film list
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlContains("/"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class, 'film-title') and contains(text(), 'El Viaje de Chihiro - part 2')]")));
        WebElement filmRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class, 'film-title') and contains(text(), 'El Viaje de Chihiro - part 2')]")));
        assertTrue(filmRow.isDisplayed(), "The film was not edited correctly.");
        
        // Delete the film
        filmRow.click(); // Follow to film details page
        deleteFilm("//*[contains(text(), \"Film 'El Viaje de Chihiro - part 2' deleted\")]");

    }

    private void deleteFilm(String xPathExpression) {
        // Wait for the detail page to load
        wait.until(ExpectedConditions.urlContains("/films/"));
        // Locate and click the delete button
        WebElement deleteButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("remove-film")));
        deleteButton.click();
        // Wait for the URL to change to the delete confirmation page
        wait.until(ExpectedConditions.urlContains("/delete"));
        // Wait for the deletion confirmation message to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPathExpression)));
        // Click on the "Return to list" button
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();
    }

    private void clickOnSaveButton() {
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("Save")));
        saveButton.click();
    }
}