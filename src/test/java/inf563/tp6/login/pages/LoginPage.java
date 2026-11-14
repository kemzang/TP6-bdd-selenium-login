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
        Demo.taper(driver, champEmail, email);
        WebElement champMdp = driver.findElement(CHAMP_MOT_DE_PASSE);
        champMdp.clear();
        Demo.taper(driver, champMdp, motDePasse);
        WebElement bouton = driver.findElement(BOUTON_SE_CONNECTER);
        Demo.surligner(driver, bouton);
        bouton.click();
    }

    // ---------- Double authentification (2FA) ----------

    private static final By CHAMP_CODE_2FA = By.cssSelector(
            "input[autocomplete='one-time-code'], input[type='tel'], input[type='number'], input[type='text']");

    /** Chemin de l'URL sans les parametres (la page d'accueil peut contenir "?checkpoint_src=..."). */
    private static String chemin(String url) {
        return java.net.URI.create(url).getPath();
    }

    private static boolean urlDoubleAuthentification(String url) {
        String chemin = chemin(url);
        return chemin.contains("two_step_verification") || chemin.contains("checkpoint")
                || chemin.contains("approvals") || chemin.contains("two_factor");
    }

    public boolean pageDoubleAuthentificationAffichee() {
        return urlDoubleAuthentification(driver.getCurrentUrl());
    }

    /** Attend la page "Verification en deux etapes" de Facebook. */
    public boolean estSurPageDoubleAuthentification() {
        try {
            boolean ok = wait.until(d -> urlDoubleAuthentification(d.getCurrentUrl()));
            gererCaptcha();
            return ok;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // ---------- CAPTCHA anti-robot ----------

    private boolean captchaAffiche() {
        String texte = driver.findElement(By.tagName("body")).getText();
        return texte.contains("caractères que vous voyez") || texte.contains("characters you see");
    }

    /**
     * Facebook affiche un CAPTCHA quand il soupconne un robot (connexions automatisees repetees).
     * Un CAPTCHA est fait pour bloquer les robots : on ne le contourne pas, on laisse l'humain
     * le resoudre (mode demo / navigateur visible), puis le test continue tout seul.
     */
    public void gererCaptcha() {
        if (!captchaAffiche()) {
            return;
        }
        if (!Demo.ACTIF && "true".equals(System.getProperty("headless", "true"))) {
            throw new IllegalStateException("Facebook affiche un CAPTCHA (protection anti-robot). "
                    + "Relancer avec -Ddemo=true pour le resoudre a la main.");
        }
        System.out.println(">>> CAPTCHA Facebook : tapez les caracteres dans Chrome puis Continuer (3 minutes) <<<");
        new WebDriverWait(driver, Duration.ofMinutes(3)).until(d -> !captchaAffiche());
    }

    /**
     * Facebook affiche d'abord "Verifiez vos notifications sur un autre appareil".
     * On passe par "Essayer d'une autre maniere" puis "Application d'authentification" puis "Continuer".
     */
    public WebElement champCode2FA() {
        gererCaptcha();
        WebElement champ = premierVisible(CHAMP_CODE_2FA);
        if (champ != null) {
            return champ;
        }
        if (cliquerSiPresent("autre mani", "autre méthode", "another way")) {
            // Fenetre "Choisissez un moyen de confirmer qu'il s'agit de vous" : on attend qu'elle soit chargee.
            wait.until(d -> cliquerSiPresent("pplication d’authentification", "pplication d'authentification",
                    "uthentication app"));
            wait.until(d -> cliquerSiPresent("Continuer", "Continue"));
        }
        return wait.until(d -> premierVisible(CHAMP_CODE_2FA));
    }

    public void saisirCode2FA(String code) {
        WebElement champ = champCode2FA();
        champ.clear();
        Demo.taper(driver, champ, code);
        champ.sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    /** Apres un code faux : on reste sur la page 2FA avec un message d'erreur. */
    public String messageErreur2FA() {
        String[] motsCles = {"incorrect", "invalide", "ne correspond", "n’est pas valide", "réessayer",
                "isn’t right", "doesn’t match", "try again", "invalid"};
        return wait.until(d -> {
            for (WebElement el : d.findElements(By.xpath("//span|//div[not(*)]"))) {
                String texte = el.getText();
                for (String mot : motsCles) {
                    if (texte != null && texte.toLowerCase().contains(mot) && el.isDisplayed()) {
                        return texte.trim();
                    }
                }
            }
            return null;
        });
    }

    /** Page d'accueil = plus de formulaire de connexion ni de page de verification. */
    public boolean estSurPageAccueil(java.time.Duration delai) {
        gererCaptcha();
        try {
            return new WebDriverWait(driver, delai).until(d -> {
                String url = d.getCurrentUrl();
                if (url.contains("remember_browser")) {
                    // "Vous etes connecte(e). Faire confiance a cet appareil ?" : le Chrome de test est jetable,
                    // on ne l'ajoute pas aux appareils de confiance si Facebook propose de refuser.
                    if (!cliquerSiPresent("Ne pas faire confiance", "Pas maintenant", "Don’t trust", "Not now")) {
                        cliquerSiPresent("Faire confiance à cet appareil", "Trust this device");
                    }
                    return false;
                }
                return url.contains("facebook.com") && !chemin(url).contains("/login")
                        && !urlDoubleAuthentification(url) && d.findElements(CHAMP_MOT_DE_PASSE).isEmpty();
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    private WebElement premierVisible(By selecteur) {
        for (WebElement el : driver.findElements(selecteur)) {
            if (el.isDisplayed() && el.isEnabled()) {
                return el;
            }
        }
        return null;
    }

    /**
     * Clique sur l'element visible dont le texte contient l'un des morceaux donnes.
     * On prefere un element cliquable (bouton, option radio, lien) contenant ce texte, pour ne pas
     * cliquer sur un titre ; sinon l'element qui porte directement le texte.
     */
    private boolean cliquerSiPresent(String... morceaux) {
        for (String morceau : morceaux) {
            String t = "contains(., \"" + morceau + "\")";
            List<WebElement> candidats = new java.util.ArrayList<>(driver.findElements(By.xpath(
                    "//*[@role='button' or self::button or @role='radio' or self::label or @role='link']"
                            + "[.//text()[" + t + "]]")));
            java.util.Collections.reverse(candidats); // le plus interieur d'abord
            candidats.addAll(driver.findElements(By.xpath("//*[text()[" + t + "]]")));
            for (WebElement el : candidats) {
                try {
                    if (el.isDisplayed()) {
                        Demo.surligner(driver, el);
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el);
                        return true;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    // la page a change pendant la recherche : on essaie le suivant
                }
            }
        }
        return false;
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
