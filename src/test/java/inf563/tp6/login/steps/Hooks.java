package inf563.tp6.login.steps;

import inf563.tp6.login.pages.Demo;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

public class Hooks {

    public static WebDriver driver;
    public static boolean headless;

    @Before
    public void demarrerNavigateur() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // Chrome visible avec -Dheadless=false, ou avec -Ddemo=true (mode demo au ralenti).
        headless = !Demo.ACTIF && !"false".equals(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
            // En headless, Chrome s'annonce "HeadlessChrome" : on garde un user-agent de Chrome normal
            // pour que Facebook serve la meme page qu'a un utilisateur.
            options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        } else {
            // Sous Ubuntu/Wayland, Chrome visible peut planter au demarrage (bug GTK) :
            // on force le backend X11 (XWayland) qui fonctionne.
            options.addArguments("--ozone-platform=x11");
        }
        options.addArguments("--window-size=1280,900", "--lang=fr-FR");
        // Page Facebook en francais, meme depuis la CI GitHub (serveurs aux USA).
        options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "fr-FR,fr"));
        driver = new ChromeDriver(options);
    }

    /** Mode demo : pause apres chaque etape pour laisser le temps de voir le resultat. */
    @AfterStep
    public void pauseDemo(Scenario scenario) {
        Demo.pause();
        // Capture d'ecran de chaque etape dans le rapport HTML (target/cucumber-report.html).
        if (driver != null) {
            scenario.attach(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png", "etape");
        }
    }

    @After
    public void fermerNavigateur(Scenario scenario) {
        if (driver == null) {
            return;
        }
        // Capture d'ecran jointe au rapport quand un scenario echoue.
        if (scenario.isFailed()) {
            System.out.println("ECHEC sur l'URL : " + driver.getCurrentUrl()
                    + " | titre : " + driver.getTitle());
            byte[] capture = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(capture, "image/png", "capture-echec");
        }
        driver.quit();
    }
}
