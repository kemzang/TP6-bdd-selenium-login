package inf563.tp6.login.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Page Object pour la page de connexion de Facebook (https://www.facebook.com/). */
public class LoginPage {

    private static final String URL = "https://www.facebook.com/";

    // Facebook genere des id aleatoires : on cible donc les attributs stables (name, aria-label).
    private static final By CHAMP_EMAIL = By.name("email");
    private static final By CHAMP_MOT_DE_PASSE = By.name("pass");
    private static final By BOUTON_SE_CONNECTER = By.cssSelector(
            "[role='button'][aria-label='Se connecter'], button[name='login'], input[type='submit']");
    // Apres un echec, Facebook marque le champ fautif avec aria-invalid="true".
    private static final By CHAMP_EN_ERREUR = By.cssSelector("input[aria-invalid='true']");
    // Variante "bandeau" : bloc de texte au-dessus des champs.
    private static final By BANDEAU_ERREUR = By.xpath(
            "//span[contains(., 'informations de connexion') or contains(., 'incorrect')"
                    + " or contains(., 'pas associé') or contains(., 'incorrect')"
                    + " or contains(., 'isn’t connected') or contains(., 'credentials')]");
    // Banniere cookies (affichee surtout en Europe) : on la ferme si elle apparait.
    private static final By BOUTON_COOKIES = By.xpath(
            "//*[@role='button' or self::button][contains(., 'Autoriser tous les cookies')"
                    + " or contains(., 'Refuser les cookies optionnels')"
                    + " or contains(., 'Allow all cookies')"
                    + " or contains(., 'Decline optional cookies')]");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void open() {
        driver.get(URL);
        fermerBanniereCookies();
        wait.until(ExpectedConditions.visibilityOfElementLocated(CHAMP_EMAIL));
    }

    private void fermerBanniereCookies() {
        List<WebElement> boutons = driver.findElements(BOUTON_COOKIES);
        if (!boutons.isEmpty() && boutons.get(0).isDisplayed()) {
            boutons.get(0).click();
        }
    }

    public boolean formulaireEstAffiche() {
        return driver.findElement(CHAMP_EMAIL).isDisplayed()
                && driver.findElement(CHAMP_MOT_DE_PASSE).isDisplayed()
                && driver.findElement(BOUTON_SE_CONNECTER).isDisplayed();
    }

    /** Remplit les champs et clique sur "Se connecter". */
    public void login(String email, String motDePasse) {
        WebElement champEmail = driver.findElement(CHAMP_EMAIL);
        champEmail.clear();
        champEmail.sendKeys(email);
        WebElement champMdp = driver.findElement(CHAMP_MOT_DE_PASSE);
        champMdp.clear();
        champMdp.sendKeys(motDePasse);
        driver.findElement(BOUTON_SE_CONNECTER).click();
    }

    /**
     * Attend le message d'erreur de Facebook et le renvoie. Facebook l'affiche de deux facons
     * selon le pays / la variante de page servie :
     *  - sous le champ fautif (champ marque aria-invalid="true", message relie par aria-describedby) ;
     *  - dans un bandeau en haut du formulaire ("Les informations de connexion ... sont incorrectes").
     */
    public String messageErreur() {
        return wait.until(d -> {
            for (WebElement champ : d.findElements(CHAMP_EN_ERREUR)) {
                String idMessage = champ.getAttribute("aria-describedby");
                if (champ.isDisplayed() && idMessage != null && !d.findElements(By.id(idMessage)).isEmpty()) {
                    String texte = d.findElement(By.id(idMessage)).getText().trim();
                    if (!texte.isEmpty()) {
                        return texte;
                    }
                }
            }
            for (WebElement bandeau : d.findElements(BANDEAU_ERREUR)) {
                if (bandeau.isDisplayed() && !bandeau.getText().isBlank()) {
                    return bandeau.getText().trim();
                }
            }
            return null; // pas encore affiche : on reessaie
        });
    }

    /** Connecte = le formulaire de connexion a disparu de la page. */
    public boolean estConnecte() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(CHAMP_MOT_DE_PASSE));
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String urlCourante() {
        return driver.getCurrentUrl();
    }

    public boolean estToujoursSurLaPageDeConnexion() {
        return !driver.findElements(CHAMP_MOT_DE_PASSE).isEmpty()
                && driver.findElement(CHAMP_MOT_DE_PASSE).isDisplayed();
    }
}
