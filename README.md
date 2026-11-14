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
| 2FA - Facebook demande le code (`@compte-2fa @2fa-demande`) | non | page « vérification en deux étapes » et champ du code affichés |
| 2FA - code incorrect `000000` (`@compte-2fa @2fa-code-faux`) | non | message d'erreur, on reste sur la page 2FA |
| 2FA - bon code (`@compte-2fa @2fa-succes`) | non | redirection vers la page d'accueil |

Les scénarios par défaut n'utilisent aucun vrai compte : uniquement des
adresses en `@example.com`, un domaine réservé qui n'appartient à personne.
N'utilisez pas d'identifiants inventés comme `abc` : ils peuvent correspondre
au compte de quelqu'un d'autre.

## Structure

- `src/test/resources/features/login.feature` - scénarios Gherkin en français.
- `src/test/java/.../pages/LoginPage.java` - Page Object (sélecteurs et actions sur la page Facebook).
- `src/test/java/.../steps/LoginSteps.java` - step definitions (Gherkin vers Java).
- `src/test/java/.../pages/Demo.java` - mode démo au ralenti ; `Totp.java` - calcul du code 2FA.
- `src/test/java/.../steps/Hooks.java` - démarre/ferme Chrome avant/après chaque scénario, capture d'écran en cas d'échec.
- `src/test/java/.../RunCucumberTest.java` - point d'entrée JUnit Platform.
- `src/test/resources/junit-platform.properties` - exclut `@compte-reel` par défaut.
- `.github/workflows/ci.yml` - lance `mvn test` (Chrome headless) à chaque push.

## Commandes

```bash
mvn test                      # scénarios sans compte, Chrome invisible (comme la CI)
mvn test -Dheadless=false     # Chrome visible, vitesse normale
mvn test -Ddemo=true          # MODE DÉMO : Chrome visible, saisie lettre par lettre,
                              # champs encadrés en rouge, pause de 2 s après chaque étape
mvn test -Ddemo=true -Ddemo.pause=4000   # pauses de 4 s
```

### Avec un vrai compte

Les identifiants restent dans le terminal, jamais dans le code ni sur GitHub.

```bash
export FB_EMAIL="mon.email@exemple.com"
export FB_PASSWORD="mon_mot_de_passe"

# Compte SANS double authentification : succès + mauvais mot de passe
mvn test -Ddemo=true -Dcucumber.filter.tags=@compte-reel

# Compte AVEC double authentification
mvn test -Ddemo=true -Dcucumber.filter.tags=@compte-2fa
```

Pour le code 2FA, deux possibilités :
- **Saisie manuelle** (mode démo) : au moment du code, le test attend 2 minutes que
  vous tapiez le code (ou approuviez sur le téléphone) dans la fenêtre Chrome.
- **Automatique** : `export FB_TOTP_SECRET="ABCD EFGH ..."` avec la clé de
  l'application d'authentification (affichée par Facebook quand on active la 2FA
  par application, sous le QR code). Le test calcule le code lui-même (`Totp.java`, RFC 6238).

Un seul scénario 2FA : `-Dcucumber.filter.tags=@2fa-succes` (ou `@2fa-demande`, `@2fa-code-faux`).

### Parcours 2FA automatisé

1. Connexion e-mail + mot de passe.
2. Facebook propose « Vérifiez vos notifications sur un autre appareil » : le robot clique
   « Essayer d'une autre manière », choisit « Application d'authentification », puis « Continuer ».
3. Le robot calcule le code à 6 chiffres (`Totp.java`) et le saisit.
4. Écran « Faire confiance à cet appareil ? » : le robot le passe.
5. Vérification : on est sur la page d'accueil.

### CAPTCHA

Après plusieurs connexions automatisées, Facebook affiche un CAPTCHA (« Entrez les
caractères que vous voyez »). Un CAPTCHA sert précisément à bloquer les robots : le test
ne le contourne pas. En mode démo, il s'arrête, affiche un message dans le terminal et
attend (3 minutes) que la personne le résolve dans Chrome, puis reprend. Sans
navigateur visible, le test échoue avec un message explicite.

Chaque étape est capturée dans le rapport `target/cucumber-report.html`.

Lancer ces scénarios ponctuellement : des connexions automatisées répétées
peuvent déclencher une vérification de sécurité Facebook (CAPTCHA).
