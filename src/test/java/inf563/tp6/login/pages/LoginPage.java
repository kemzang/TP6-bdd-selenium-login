package inf563.tp6.login.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object pour https://the-internet.herokuapp.com/login (fixture publique Selenium). */
public class LoginPage {

    private static final String URL = "https://the-internet.herokuapp.com/login";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }

    public void login(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("form button[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
    }

    public boolean estSurLaZoneSecurisee() {
        return driver.getCurrentUrl().endsWith("/secure");
    }

    public String messageFlash() {
        WebElement flash = driver.findElement(By.id("flash"));
        return flash.getText().replace("×", "").trim();
    }

    public boolean messageFlashEstUneErreur() {
        String classes = driver.findElement(By.id("flash")).getAttribute("class");
        return classes != null && classes.contains("error");
    }

    public boolean messageFlashEstUnSucces() {
        String classes = driver.findElement(By.id("flash")).getAttribute("class");
        return classes != null && classes.contains("success");
    }
}
