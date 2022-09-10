# Erreurs courantes

## Registre non lancé
#dev/erreur
`Could not locate PropertySource: I/O error on GET request for "http://localhost:8761/config/monolith/dev/main": Connexion refusée (Connection refused); nested exception is java.net.ConnectException: Connexion refusée (Connection refused)`
lancer le registre

## Database credentials not filled in

`org.postgresql.util.PSQLException: The server requested password-based authentication, but no password was provided by plugin null`

Dans application-dev.yml, il faut renseigner le champ datasource.password

