#!/usr/bin/env bash
# Demo complete pour la presentation, en UNE commande : ./demo.sh
#  1. cas sans compte : formulaire, e-mail inconnu, champs vides
#  2. avec mon compte : mauvais mot de passe, code 2FA incorrect
#  3. succes a la fin : bon code 2FA -> page d'accueil
# Chrome visible, au ralenti. Identifiants lus dans ~/.fb-test.env (hors du projet, jamais sur GitHub).
set -e
cd "$(dirname "$0")"

if [ -f ~/.fb-test.env ]; then
  # lecture ligne par ligne (la cle 2FA peut contenir des espaces)
  while IFS='=' read -r cle valeur; do
    [ -n "$cle" ] && export "$cle=$valeur"
  done < ~/.fb-test.env
else
  echo "~/.fb-test.env introuvable : seuls les cas sans compte vont reussir." >&2
fi

mvn test -Ddemo=true -Dcucumber.filter.tags="not @compte-sans-2fa and not @2fa-demande" "$@"

echo
echo "Rapport avec une capture par etape : target/cucumber-report.html"
