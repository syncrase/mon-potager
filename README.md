# monolith

This application was generated using JHipster 7.8.1, you can find documentation and help at [https://www.jhipster.tech](https://www.jhipster.tech).

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husk, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if ommited) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files

- `npmw` - wrapper to use locally installed npm.
  JHipster installs Node and npm locally using the build tool by default. This wrapper makes sure npm is installed locally and uses it avoiding some differences different versions can cause. By using `./npmw` instead of the traditional `npm` you can configure a Node-less environment to develop or test your application.
- `/src/main/docker` - Docker configurations for the application and services that the application depends on


#jhipster #projet/potager

# B.A.BA

```bash
# Génération du projet
mkdir monolith
cd monolith
jhipster# Répondre aux questions
npm install# A cause de l'erreur rimraf

# Import du JDL
jhipster jdl ../meta-plants/JDLs/monolith.jdl

# Lancer le registre
./Development/sources/jhipster-registry/mvnw

# Créer l'utilisateur et la basesudo -i -u postgres
psql
CREATE USER monolith;
ALTER ROLE monolith WITH createdb;
ALTER ROLE monolith WITH PASSWORD 'monolith';
CREATE DATABASE monolith;

# Renseigner datasource.password dans application-dev.yml

# Exécution du projet
./mvnw
```

# Erreur courante

## Registre non lancé

`Could not locate PropertySource: I/O error on GET request for "http://localhost:8761/config/monolith/dev/main": Connexion refusée (Connection refused); nested exception is java.net.ConnectException: Connexion refusée (Connection refused)`
lancer le registre

## Database credentials not filled in

`org.postgresql.util.PSQLException: The server requested password-based authentication, but no password was provided by plugin null`

Dans application-dev.yml, il faut renseigner le champ datasource.password

