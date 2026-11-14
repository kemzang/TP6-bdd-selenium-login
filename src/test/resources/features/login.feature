# language: fr
# Site cible : https://www.facebook.com/ (page de connexion).
#
# Scenarios sans tag : aucun vrai compte, uniquement des e-mails en @example.com
# (domaine reserve, qui ne peut appartenir a personne). Lances a chaque "mvn test" et en CI.
#
# Scenarios @compte-reel : utilisent un vrai compte (variables FB_EMAIL / FB_PASSWORD).
# Lances uniquement sur demande : mvn test -Dcucumber.filter.tags=@compte-reel
Fonctionnalité: Connexion à Facebook
  Afin de valider le comportement du formulaire de connexion de Facebook
  En tant que testeur
  Je veux automatiser les cas de succès et d'échec de la connexion avec Selenium

  Contexte:
    Soit la page de connexion Facebook est ouverte

  Scénario: Le formulaire de connexion est affiché
    Alors le formulaire de connexion est affiché

  Scénario: Refus - e-mail qui n'existe pas
    Quand je me connecte avec l'e-mail "test.inf563.tp6@example.com" et le mot de passe "mauvais_mot_de_passe"
    Alors un message d'erreur est affiché sous le champ "email"
    Et je reste sur la page de connexion

  Plan du scénario: Refus - champs vides (<cas>)
    Quand je me connecte avec l'e-mail "<email>" et le mot de passe "<mot_de_passe>"
    Alors un message d'erreur est affiché sous le champ "email"
    Et je reste sur la page de connexion

    Exemples:
      | cas                    | email                       | mot_de_passe |
      | tous les champs vides  |                             |              |
      | e-mail vide            |                             | unMotDePasse |
      | mot de passe vide      | test.inf563.tp6@example.com |              |

  @compte-reel
  Scénario: Succès - connexion avec mon compte
    Quand je me connecte avec mon compte Facebook
    Alors je suis connecté et redirigé vers ma page d'accueil

  @compte-reel
  Scénario: Refus - mon e-mail avec un mauvais mot de passe
    Quand je me connecte avec mon e-mail et le mot de passe "mauvais_mot_de_passe_123"
    Alors un message d'erreur est affiché sous le champ "pass"
    Et je reste sur la page de connexion
