# Organisation des branches

Différentes branches permettent d'utiliser Jhipster sans avoir à cherry-pick les modification des fichiers générés.

Les branches sont organisées de la manière suivante :

- branche 'socle-jhipster' ne contenant rien d'autre que la génération de base de jhipster
- branche 'jhipster-jdl' contenant en plus la génération du modèle
- branches 'feature/add-plante' contenant la fonctionnalité add-plante
- branche 'release' qui est la branche par défaut. Elle contient le marge de toutes les branches features

Les trois 1ières branches sont 'basées' les unes sur les autres, ainsi :
socle-jhipster -> jhipster-jdl -> feature/<feature_name>

# Exemple de génération de modèle

Pour modifier le modèle :

```bash
git fetch --all
git checkout jhipster-jdl
git revert <last_jdl_generation>
# modifier le JDL
git add <jdl> && git commit -m "jdl vX"
jhipster jdl ../meta-plants/JDLs/monolith.jdl
git add . && git commit -m "jdl generation for vX" && git push --force

git checkout feature/add-plante
git rebase jhipster-jdl
```

Le principe est le même pour la modification du socle.

# Exemple de récupération de code

Parceque la gestion des branches se base sur des rebase successifs, il ne faut pas utiliser de git pull (fetch &&
checkout), mais plutôt utiliser fetch && reset --force pour écraser l'index local.

```bash
# Aucune modification en local
# Commencer les reset hard sur la base modifiée
# Dans le cas où le socle à été modifé
git checkout socle-jhipster # positionnement sur la base avant le fetch
git fetch --all # Récupérer l'index du remote
git reset --hard socle-jhipster # Ecraser l'index local pour cette branche
git checkout jhipster-jdl # Postionnement sur la branche "supérieure"
git reset --hard jhipster-jdl # Ecrasement de l'index local
git checkout feature/add-plante # idem
git reset --hard feature/add-plante # idem
```
