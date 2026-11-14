package inf563.tp6.login.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Mode demo (-Ddemo=true) : ralentit le robot pour que le public voie ce qui se passe.
 * Saisie lettre par lettre, element surligne avant chaque action, pause apres chaque etape.
 * Desactive par defaut (et donc en CI) : les tests restent rapides.
 */
public final class Demo {

    public static final boolean ACTIF = Boolean.parseBoolean(System.getProperty("demo", "false"));
    private static final long PAUSE_ETAPE_MS = Long.getLong("demo.pause", 2000);

    private Demo() {
    }

    public static void pause() {
        if (ACTIF) {
            dormir(PAUSE_ETAPE_MS);
        }
    }

    /** Encadre l'element en rouge (mode demo uniquement). */
    public static void surligner(WebDriver driver, WebElement element) {
        if (ACTIF) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.outline='3px solid red'; arguments[0].style.outlineOffset='2px';", element);
            dormir(500);
        }
    }

    /** Tape le texte d'un coup, ou lettre par lettre en mode demo. */
    public static void taper(WebDriver driver, WebElement champ, String texte) {
        surligner(driver, champ);
        if (!ACTIF) {
            champ.sendKeys(texte);
            return;
        }
        for (char c : texte.toCharArray()) {
            champ.sendKeys(String.valueOf(c));
            dormir(120);
        }
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
