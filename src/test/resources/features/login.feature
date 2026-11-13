# language: fr
# Site cible : https://the-internet.herokuapp.com/login - fixture publique
# maintenue par Elemental Selenium/Sauce Labs, construite exprès pour
# l'entrainement a l'automatisation (identifiants de test affiches sur la
# page elle-meme). Utilise a la place d'un vrai compte Facebook pour ne pas
# automatiser une authentification sur un service tiers en production.
Fonctionnalité: Connexion sur un formulaire d'authentification
  Afin de valider le comportement d'un formulaire de connexion
  En tant que testeur
  Je veux automatiser des scenarios de connexion avec Selenium

  Contexte:
    Soit la page de connexion est ouverte

  Scénario: Connexion avec des identifiants valides
    Quand je me connecte avec l'utilisateur "tomsmith" et le mot de passe "SuperSecretPassword!"
    Alors je suis redirigé vers la zone sécurisée
    Et un message de succès est affiché

  Scénario: Connexion avec un nom d'utilisateur invalide
    Quand je me connecte avec l'utilisateur "mauvais_utilisateur" et le mot de passe "SuperSecretPassword!"
    Alors un message d'erreur indiquant que le nom d'utilisateur est invalide est affiché

  Scénario: Connexion avec un mot de passe invalide
    Quand je me connecte avec l'utilisateur "tomsmith" et le mot de passe "mauvais_mot_de_passe"
    Alors un message d'erreur indiquant que le mot de passe est invalide est affiché
