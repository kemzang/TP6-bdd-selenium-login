# TP6 - BDD/Selenium sur un formulaire de connexion

## Pourquoi pas le vrai facebook.com ?

La consigne initiale demandait de tester "la connexion sur notre compte
Facebook". Automatiser une tentative de connexion - meme avec un mot de passe
invalide - sur le vrai facebook.com pose deux problemes qui ne dependent pas
de ce qui est demande par le professeur :

- Les CGU de Facebook interdisent l'acces automatise/scripte a leurs pages
  d'authentification.
- Le TP doit tourner dans une pipeline GitHub Actions a chaque `push` : un
  script qui frappe le vrai formulaire de connexion de Facebook a chaque push
  est exactement le type de trafic que leurs systemes anti-bot sont concus
  pour detecter et bloquer (risque de blocage d'IP/compte, CAPTCHA, etc.).

Ce depot utilise donc **https://the-internet.herokuapp.com/login**, une
fixture publique maintenue par Elemental Selenium / Sauce Labs, construite
explicitement pour s'entrainer a l'automatisation Selenium (les identifiants
de test sont affiches en clair sur la page elle-meme : `tomsmith` /
`SuperSecretPassword!`). Les competences evaluees sont identiques : Gherkin,
Selenium WebDriver, Page Object, assertions sur les cas de succes et
d'echec.

## Structure

- `src/test/resources/features/login.feature` - scenarios Gherkin (3 cas :
  connexion valide, nom d'utilisateur invalide, mot de passe invalide).
- `src/test/java/.../pages/LoginPage.java` - Page Object encapsulant les
  selecteurs et actions Selenium.
- `src/test/java/.../steps/Hooks.java` - demarre/ferme Chrome (headless par
  defaut) avant/apres chaque scenario.
- `src/test/java/.../steps/LoginSteps.java` - step definitions reliant le
  Gherkin au Page Object.
- `src/test/java/.../RunCucumberTest.java` - point d'entree JUnit Platform
  qui decouvre et execute les `.feature`.
- `.github/workflows/ci.yml` - execute `mvn test` (Chrome headless) a chaque
  push.

## Lancer les tests

```bash
mvn test
```

Pour observer Chrome pendant l'execution (navigateur visible, utile en
local) :

```bash
mvn test -Dheadless=false
```
