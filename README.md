Lien Docker Hub : https://hub.docker.com/r/jboss/keycloak/
Commande pour lancer le server Keycloak : docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin jboss/keycloak

API sur le port 8081 : 
GET - http://localhost:8081/auth/realms/APP/protocol/openid-connect/auth?email=a.george@monaco-telecom.mc
POST - http://localhost:8081/auth/realms/APP/protocol/openid-connect/auth

Connexion APP : http://localhost:8080/auth/realms/APP/account
Connexion ADMIN : http://localhost:8080/auth/

KEYCLOAK : https://www.keycloak.org/

Github: 
https://github.com/keycloak/keycloak-quickstarts
https://github.com/keycloak/keycloak




