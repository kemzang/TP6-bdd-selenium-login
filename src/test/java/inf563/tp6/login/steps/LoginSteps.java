package inf563.tp6.login.steps;

import inf563.tp6.login.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private LoginPage loginPage;

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

    @Then("un message d'erreur est affiché sous le champ {string}")
    public void unMessageDErreurEstAffiche(String champ) {
        assertEquals(champ, loginPage.nomDuChampEnErreur(), "le champ en erreur n'est pas le bon");
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
        assertTrue(loginPage.estConnecte(), "le formulaire de connexion aurait du disparaitre");
        String url = loginPage.urlCourante();
        System.out.println("URL apres connexion : " + url);
        assertFalse(url.contains("two_step_verification") || url.contains("checkpoint"),
                "Facebook demande une verification de securite (2FA/checkpoint) : " + url);
        assertFalse(url.contains("/login"), "on devrait etre sur la page d'accueil : " + url);
    }
}
