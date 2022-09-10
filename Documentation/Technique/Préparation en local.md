#jhipster #projet/potager

# B.A.BA
#dev/init-projet 

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
