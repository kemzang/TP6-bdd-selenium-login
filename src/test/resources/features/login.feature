# language: fr
# Site cible : https://www.facebook.com/ (page de connexion).
# Les scenarios s'executent dans l'ordre du fichier : d'abord les refus, le succes a la fin.
#
# Sans tag : aucun vrai compte, uniquement des e-mails en @example.com (domaine reserve,
#   qui ne peut appartenir a personne). Lances a chaque "mvn test" et en CI.
# @compte-reel : utilisent mon vrai compte (FB_EMAIL / FB_PASSWORD / FB_TOTP_SECRET).
#   Jamais lances par defaut ni en CI.
#
# Presentation (tout, dans l'ordre, une seule commande) : ./demo.sh
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
    Alors un message d'erreur est affiché
    Et je reste sur la page de connexion

  Plan du scénario: Refus - champs vides (<cas>)
    Quand je me connecte avec l'e-mail "<email>" et le mot de passe "<mot_de_passe>"
    Alors un message d'erreur est affiché
    Et je reste sur la page de connexion

    Exemples:
      | cas                    | email                       | mot_de_passe |
      | tous les champs vides  |                             |              |
      | e-mail vide            |                             | unMotDePasse |
      | mot de passe vide      | test.inf563.tp6@example.com |              |

  # ----- Avec mon vrai compte (double authentification activee) -----

  @compte-reel @mauvais-mdp
  Scénario: Refus - mon e-mail avec un mauvais mot de passe
    Quand je me connecte avec mon e-mail et le mot de passe "mauvais_mot_de_passe_123"
    Alors un message d'erreur est affiché
    Et je reste sur la page de connexion

  @compte-reel @compte-2fa @2fa-demande
  Scénario: Double authentification - Facebook demande le code
    Quand je me connecte avec mon compte Facebook
    Alors Facebook demande la double authentification
    Et le champ de saisie du code est affiché

  @compte-reel @compte-2fa @2fa-code-faux
  Scénario: Double authentification - refus d'un code incorrect
    Quand je me connecte avec mon compte Facebook
    Et Facebook demande la double authentification
    Et je saisis le code de double authentification "000000"
    Alors un message d'erreur sur le code est affiché
    Et je reste sur la page de double authentification

  @compte-reel @compte-2fa @2fa-succes
  Scénario: Succès - connexion avec double authentification jusqu'à la page d'accueil
    Quand je me connecte avec mon compte Facebook
    Et Facebook demande la double authentification
    Et je saisis le bon code de double authentification
    Alors je suis redirigé vers ma page d'accueil

  # Pour un compte SANS double authentification uniquement.
  @compte-reel @compte-sans-2fa
  Scénario: Succès - connexion sans double authentification
    Quand je me connecte avec mon compte Facebook
    Alors je suis connecté et redirigé vers ma page d'accueil
