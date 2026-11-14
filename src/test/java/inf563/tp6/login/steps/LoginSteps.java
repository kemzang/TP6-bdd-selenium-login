package inf563.tp6.login.steps;

import inf563.tp6.login.pages.LoginPage;
import inf563.tp6.login.pages.Totp;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private LoginPage loginPage;
    private Duration delaiAccueil = Duration.ofSeconds(20);

    @Given("la page de connexion Facebook est ouverte")
    public void laPageDeConnexionEstOuverte() {
        loginPage = new LoginPage(Hooks.driver);
        loginPage.open();
    }

    @Then("le formulaire de connexion est affiché")
    public void leFormulaireEstAffiche() {
        assertTrue(loginPage.formulaireEstAffiche(),
                "les champs e-mail, mot de passe et le bouton Se connecter devraient etre visibles");
    }

    @When("je me connecte avec l'e-mail {string} et le mot de passe {string}")
    public void jeMeConnecte(String email, String motDePasse) {
        loginPage.login(email, motDePasse);
    }

    @When("je me connecte avec mon compte Facebook")
    public void jeMeConnecteAvecMonCompte() {
        loginPage.login(variable("FB_EMAIL"), variable("FB_PASSWORD"));
    }

    @When("je me connecte avec mon e-mail et le mot de passe {string}")
    public void jeMeConnecteAvecMonEmail(String motDePasse) {
        loginPage.login(variable("FB_EMAIL"), motDePasse);
    }

    // Identifiants lus dans des variables d'environnement : jamais ecrits dans le code ni sur GitHub.
    private static String variable(String nom) {
        String valeur = System.getenv(nom);
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalStateException(
                    "Definir FB_EMAIL et FB_PASSWORD avant de lancer les scenarios @compte-reel");
        }
        return valeur;
    }

    @Then("un message d'erreur est affiché")
    public void unMessageDErreurEstAffiche() {
        String message = loginPage.messageErreur();
        System.out.println("Message affiche par Facebook : " + message);
        assertFalse(message.isEmpty(), "Facebook devrait afficher un message d'erreur");
    }

    @Then("je reste sur la page de connexion")
    public void jeResteSurLaPageDeConnexion() {
        assertTrue(loginPage.estToujoursSurLaPageDeConnexion(),
                "le formulaire de connexion devrait toujours etre affiche");
    }

    @Then("je suis connecté et redirigé vers ma page d'accueil")
    public void jeSuisConnecte() {
        boolean accueil = loginPage.estSurPageAccueil(Duration.ofSeconds(20));
        String url = loginPage.urlCourante();
        System.out.println("URL apres connexion : " + url);
        assertFalse(loginPage.pageDoubleAuthentificationAffichee(),
                "Facebook demande la double authentification : utiliser le scenario @compte-2fa. URL : " + url);
        assertTrue(accueil, "on devrait etre sur la page d'accueil : " + url);
    }

    // ---------- Double authentification (2FA) ----------

    @Then("Facebook demande la double authentification")
    public void facebookDemandeLaDoubleAuthentification() {
        assertTrue(loginPage.estSurPageDoubleAuthentification(),
                "la page de verification en deux etapes aurait du s'afficher : " + loginPage.urlCourante());
        System.out.println("Page 2FA : " + loginPage.urlCourante());
    }

    @Then("le champ de saisie du code est affiché")
    public void leChampDuCodeEstAffiche() {
        assertTrue(loginPage.champCode2FA().isDisplayed(), "le champ du code a 6 chiffres devrait etre visible");
    }

    @When("je saisis le code de double authentification {string}")
    public void jeSaisisLeCode(String code) {
        loginPage.saisirCode2FA(code);
    }

    @When("je saisis le bon code de double authentification")
    public void jeSaisisLeBonCode() {
        String cle = System.getenv("FB_TOTP_SECRET");
        if (cle != null && !cle.isBlank()) {
            // Code calcule automatiquement a partir de la cle de l'application d'authentification.
            loginPage.saisirCode2FA(Totp.codeActuel(cle));
        } else if (!Hooks.headless) {
            // Pas de cle : la personne qui presente tape (ou approuve) le code elle-meme dans Chrome.
            System.out.println(">>> Saisissez le code 2FA (ou approuvez sur le telephone) dans Chrome : 2 minutes <<<");
            delaiAccueil = Duration.ofMinutes(2);
        } else {
            throw new IllegalStateException(
                    "Definir FB_TOTP_SECRET, ou lancer avec -Ddemo=true pour saisir le code a la main");
        }
    }

    @Then("je suis redirigé vers ma page d'accueil")
    public void jeSuisRedirigeVersMaPageDAccueil() {
        boolean accueil = loginPage.estSurPageAccueil(delaiAccueil);
        System.out.println("URL apres 2FA : " + loginPage.urlCourante());
        assertTrue(accueil, "on devrait etre sur la page d'accueil : " + loginPage.urlCourante());
    }

    @Then("un message d'erreur sur le code est affiché")
    public void unMessageDErreurSurLeCode() {
        String message = loginPage.messageErreur2FA();
        System.out.println("Message 2FA affiche par Facebook : " + message);
        assertFalse(message.isEmpty(), "Facebook devrait refuser le code");
    }

    @Then("je reste sur la page de double authentification")
    public void jeResteSurLaPage2FA() {
        assertTrue(loginPage.estSurPageDoubleAuthentification(), "on devrait rester sur la page 2FA");
    }
}
