package inf563.tp6.login.steps;

import inf563.tp6.login.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private LoginPage loginPage;

    @Given("la page de connexion est ouverte")
    public void laPageDeConnexionEstOuverte() {
        loginPage = new LoginPage(Hooks.driver);
        loginPage.open();
    }

    @When("je me connecte avec l'utilisateur {string} et le mot de passe {string}")
    public void jeMeConnecte(String utilisateur, String motDePasse) {
        loginPage.login(utilisateur, motDePasse);
    }

    @Then("je suis redirigé vers la zone sécurisée")
    public void jeSuisRedirigeVersLaZoneSecurisee() {
        assertTrue(loginPage.estSurLaZoneSecurisee(), "l'URL devrait se terminer par /secure");
    }

    @Then("un message de succès est affiché")
    public void unMessageDeSuccesEstAffiche() {
        assertTrue(loginPage.messageFlashEstUnSucces(), "le message affiche devrait indiquer un succes");
    }

    @Then("un message d'erreur indiquant que le nom d'utilisateur est invalide est affiché")
    public void messageErreurNomUtilisateur() {
        assertTrue(loginPage.messageFlashEstUneErreur(), "un message d'erreur devrait etre affiche");
        assertTrue(loginPage.messageFlash().toLowerCase().contains("username"),
                "le message devrait mentionner le nom d'utilisateur : " + loginPage.messageFlash());
    }

    @Then("un message d'erreur indiquant que le mot de passe est invalide est affiché")
    public void messageErreurMotDePasse() {
        assertTrue(loginPage.messageFlashEstUneErreur(), "un message d'erreur devrait etre affiche");
        assertTrue(loginPage.messageFlash().toLowerCase().contains("password"),
                "le message devrait mentionner le mot de passe : " + loginPage.messageFlash());
    }
}
