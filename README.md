# TP6 - BDD/Selenium : connexion à Facebook

Tests BDD (Cucumber + Selenium WebDriver) sur la page de connexion de
**https://www.facebook.com/**. Un robot ouvre Chrome, remplit les champs,
clique sur « Se connecter » et vérifie le résultat.

## Scénarios

| Scénario | Lancé par défaut | Ce qui est vérifié |
|---|---|---|
| Le formulaire de connexion est affiché | oui | champs e-mail, mot de passe et bouton visibles |
| Refus - e-mail qui n'existe pas | oui | message d'erreur affiché, on reste sur la page |
| Refus - tous les champs vides | oui | message d'erreur affiché, on reste sur la page |
| Refus - e-mail vide | oui | message d'erreur affiché, on reste sur la page |
| Refus - mot de passe vide | oui | message d'erreur affiché, on reste sur la page |
| Succès - connexion avec mon compte (`@compte-reel`) | non | le formulaire disparaît, on arrive sur la page d'accueil |
| Refus - mon e-mail + mauvais mot de passe (`@compte-reel`) | non | message d'erreur affiché, on reste sur la page |

Les scénarios par défaut n'utilisent aucun vrai compte : uniquement des
adresses en `@example.com`, un domaine réservé qui n'appartient à personne.
N'utilisez pas d'identifiants inventés comme `abc` : ils peuvent correspondre
au compte de quelqu'un d'autre.

## Structure

- `src/test/resources/features/login.feature` - scénarios Gherkin en français.
- `src/test/java/.../pages/LoginPage.java` - Page Object (sélecteurs et actions sur la page Facebook).
- `src/test/java/.../steps/LoginSteps.java` - step definitions (Gherkin vers Java).
- `src/test/java/.../steps/Hooks.java` - démarre/ferme Chrome avant/après chaque scénario, capture d'écran en cas d'échec.
- `src/test/java/.../RunCucumberTest.java` - point d'entrée JUnit Platform.
- `src/test/resources/junit-platform.properties` - exclut `@compte-reel` par défaut.
- `.github/workflows/ci.yml` - lance `mvn test` (Chrome headless) à chaque push.

## Commandes

```bash
mvn test                      # scénarios par défaut, Chrome invisible
mvn test -Dheadless=false     # Chrome visible

# Connexion avec un vrai compte (les identifiants restent dans le terminal,
# jamais dans le code ni sur GitHub) :
export FB_EMAIL="mon.email@exemple.com"
export FB_PASSWORD="mon_mot_de_passe"
mvn test -Dheadless=false -Dcucumber.filter.tags=@compte-reel
```

Le scénario `@compte-reel` est à lancer ponctuellement : des connexions
automatisées répétées peuvent déclencher une vérification de sécurité
Facebook (CAPTCHA, code de confirmation).
