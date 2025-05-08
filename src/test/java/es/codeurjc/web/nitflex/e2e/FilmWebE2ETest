package es.codeurjc.web.nitflex.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.UserComponent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmWebE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserComponent userComponent;

    @BeforeEach
    public void setup() {
        // Set up test data - create a user if needed
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName("testUser");
            user.setEmail("test@example.com");
            userRepository.save(user);
        }
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        
        // Initialize Chrome driver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        // Close browser
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 3.1 - Test to check that when a new film is created
     * (without including the image), we expect the created film 
     * to appear on the resulting screen
     */
    @Test
    public void whenCreateFilm_thenFilmAppearsInList() {
        // Create a unique title to avoid conflicts
        String filmTitle = "Test Film Creation " + System.currentTimeMillis();
        
        try {
            // Navigate to the home page
            driver.get("http://localhost:" + port + "/");
            
            // Click on "New film" button 
            WebElement newFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
            newFilmButton.click();
            
            // Fill the form - verify we are on the form page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-film")));
            
            // Fill in the form
            WebElement titleInput = driver.findElement(By.name("title"));
            titleInput.sendKeys(filmTitle);
            
            WebElement synopsisInput = driver.findElement(By.name("synopsis"));
            synopsisInput.sendKeys("Test film creation synopsis");
            
            WebElement yearInput = driver.findElement(By.name("releaseYear"));
            yearInput.clear();
            yearInput.sendKeys("2023");
            
            // Select age rating from dropdown
            driver.findElement(By.cssSelector("select[name='ageRating'] option[value='+12']")).click();
            
            // Submit the form
            WebElement submitButton = driver.findElement(By.id("Save"));
            submitButton.click();
            
            // Wait for film details page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("film-title")));
            
            // Verify that the film title appears on the details page
            WebElement filmTitleElement = driver.findElement(By.id("film-title"));
            assertEquals(filmTitle, filmTitleElement.getText(), "Film title should match in details page");
            
            // Go back to all films
            WebElement allFilmsButton = driver.findElement(By.id("all-films"));
            allFilmsButton.click();
            
            // Verify film is in the list
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")));
            
            assertTrue(isFilmInList(filmTitle), "Film should be in the list after creation");
            
            System.out.println("E2E TEST PASSED! Film '" + filmTitle + "' was successfully created and appears in the list");
        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 3.4 - Test to check that when a new film is created and edited
     * to add '- parte 2' to its title, we verify that the change has been applied
     */
    @Test
    public void whenCreateFilmAndEdit_thenFilmTitleIsUpdated() {
        // Create a unique title to avoid conflicts
        String originalFilmTitle = "Edit Film Test " + System.currentTimeMillis();
        String editedFilmTitle = originalFilmTitle + " - parte 2";
        
        try {
            // Navigate to the home page
            driver.get("http://localhost:" + port + "/");
            
            // Click on "New film" button
            WebElement newFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
            newFilmButton.click();
            
            // Verify we are on the form page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-film")));
            
            // Fill in the form
            WebElement titleInput = driver.findElement(By.name("title"));
            titleInput.sendKeys(originalFilmTitle);
            
            WebElement synopsisInput = driver.findElement(By.name("synopsis"));
            synopsisInput.sendKeys("Film to test editing functionality");
            
            WebElement yearInput = driver.findElement(By.name("releaseYear"));
            yearInput.clear();
            yearInput.sendKeys("2024");
            
            // Select age rating from dropdown
            driver.findElement(By.cssSelector("select[name='ageRating'] option[value='+12']")).click();
            
            // Submit the form
            WebElement submitButton = driver.findElement(By.id("Save"));
            submitButton.click();
            
            // Wait for film details page to load and verify we're on the correct page
            WebElement filmDetailSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filmDetail")));
            WebElement filmTitleElement = driver.findElement(By.id("film-title"));
            assertEquals(originalFilmTitle, filmTitleElement.getText(), "Film title should match in details page");
            
            // Click on the Edit button
            WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("edit-film")));
            editButton.click();
            
            // Wait for the edit form to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-film")));
            
            // Edit the title
            WebElement editTitleInput = driver.findElement(By.name("title"));
            editTitleInput.clear();
            editTitleInput.sendKeys(editedFilmTitle);
            
            // Submit the edit form
            WebElement saveEditButton = driver.findElement(By.id("Save"));
            saveEditButton.click();
            
            // Wait for updated film details page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filmDetail")));
            
            // Verify the title was updated on the details page
            WebElement updatedFilmTitleElement = driver.findElement(By.id("film-title"));
            assertEquals(editedFilmTitle, updatedFilmTitleElement.getText(), "Film title should be updated in details page");
            
            // Go back to the film list
            driver.findElement(By.id("all-films")).click();
            
            // Wait for the page to fully load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("create-film")));
            
            // Force refresh the page to ensure we're seeing the latest data
            driver.navigate().refresh();
            
            // Wait for the page to reload after refresh
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("create-film")));
            
            // Verify edited film is in the list
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + editedFilmTitle + "')]")));
            
            assertTrue(isFilmInList(editedFilmTitle), "Edited film should be in the list");
            
            // Use a more precise XPath to verify original title is not present
            int originalTitleCount = driver.findElements(
                By.xpath("//a[contains(@class, 'film-title') and text()='" + originalFilmTitle + "']")).size();
            assertEquals(0, originalTitleCount, "Original film title should not be in the list after edit");
            
            System.out.println("E2E TEST PASSED! Film '" + originalFilmTitle + "' was successfully edited to '" + editedFilmTitle + "'");
        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void whenCreateFilmAndDelete_thenFilmDisappears() {
        // Create a unique title to avoid conflicts
        String filmTitle = "Test Film " + System.currentTimeMillis();
        
        try {
            // Navigate to the home page
            driver.get("http://localhost:" + port + "/");
            
            // Click on "New film" button (using its ID from the template)
            WebElement newFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
            newFilmButton.click();
            
            // Fill the form
            WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
            titleInput.sendKeys(filmTitle);
            
            WebElement synopsisInput = driver.findElement(By.name("synopsis"));
            synopsisInput.sendKeys("Film to test deletion");
            
            WebElement yearInput = driver.findElement(By.name("releaseYear"));
            yearInput.clear();
            yearInput.sendKeys("2024");
            
            // Select age rating from dropdown
            driver.findElement(By.cssSelector("select[name='ageRating'] option[value='+12']")).click();
            
            // Submit the form (using the ID from the template)
            WebElement submitButton = driver.findElement(By.id("Save"));
            submitButton.click();
            
            // Wait for film details page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("film-title")));
            
            // Go back to all films
            WebElement allFilmsButton = driver.findElement(By.id("all-films"));
            allFilmsButton.click();
            
            // Verify film is in the list (using the class from the template)
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")));
            assertTrue(isFilmInList(filmTitle), "Film should be in the list after creation");
            
            // Click on the film to go to details page
            driver.findElement(By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")).click();
            
            // Click on the Remove button on the film details page
            WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("remove-film")));
            removeButton.click();
            
            // Wait for confirmation or redirect back to films list
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/"),
                ExpectedConditions.presenceOfElementLocated(By.id("create-film"))
            ));
            
            // Navigate back to films list if not already there
            if (!driver.getCurrentUrl().endsWith("/")) {
                driver.get("http://localhost:" + port + "/");
            }
            
            try {
                Thread.sleep(1000); // Small wait to ensure page is fully loaded
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            assertFalse(isFilmInList(filmTitle), "Film should not be in the list after deletion");
            
            System.out.println("E2E TEST PASSED! Film '" + filmTitle + "' was created and successfully deleted");
        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Checks if a film with the given title is present in the film list
     * 
     * @param filmTitle Title of the film to search for
     * @return true if the film is in the list, false otherwise
     */
    
    private boolean isFilmInList(String filmTitle) {
        try {
            return driver.findElements(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")
            ).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}