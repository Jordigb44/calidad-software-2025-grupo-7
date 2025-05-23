package es.codeurjc.web.nitflex.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.UserComponent;
import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSeleniumMultibrowser {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserComponent userComponent;

    private WebDriver driver;
    private WebDriverWait wait;

    private final String FILM_TITLE = "Selenium Movie";
    private final String FILM_SYNOPSIS = "Created by Selenium";
    private final String FILM_YEAR = "2024";

    private String getUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName("testUser");
            user.setEmail("test@example.com");
            userRepository.save(user);
        }

        String browser = System.getenv("BROWSER");
        if (browser != null && browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            
            String firefoxBin = System.getenv("FIREFOX_BIN");
            if (firefoxBin != null && !firefoxBin.isEmpty()) {
                options.setBinary(firefoxBin);
            }
            
            driver = new FirefoxDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            driver = new ChromeDriver(options);
        }
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Create movie and verify title, year, and synopsis are displayed")
    void testCreateMovieAndVerifyData() {
        driver.get(getUrl() + "/");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title"))).sendKeys(FILM_TITLE);
        driver.findElement(By.name("synopsis")).sendKeys(FILM_SYNOPSIS);
        driver.findElement(By.name("releaseYear")).sendKeys(FILM_YEAR);
        driver.findElement(By.id("Save")).click();

        wait.until(ExpectedConditions.urlContains("/films/"));
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("film-title")));

        Assertions.assertEquals(FILM_TITLE, title.getText());
        Assertions.assertTrue(driver.getPageSource().contains(FILM_YEAR));
        Assertions.assertTrue(driver.getPageSource().contains(FILM_SYNOPSIS));
    }
}
