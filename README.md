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

--------------------------

#UPDATE 21/10/20 :

* Utiliser le serveur local (pas l'image docker)
* Lancer le script ./bin/standalone.sh

##API :

Prendre keycloak du github officiel et générer un JAR de l'exemple REST :
![Image of REST JAR](https://github.com/GeorgesAlexis/api_keycloak/blob/master/artefact.png)

Déplacer le JAR dans le dossier standalone "\standalone\deployments".
Celui ci va être auto déployer, sinon restart de le serveur

##POSTMAN:
* GET TOKEN : http://localhost:8080/auth/realms/master/protocol/openid-connect/token
* GET REALM'S USERS : http://localhost:8080/auth/admin/realms/IDP1/users
* GET USER ATTRIBUTES : http://localhost:8080/auth/admin/realms/APP/users/a17e226b-8d68-4d4e-99d5-2b808b02975b
* GET IDP FROM USER : http://localhost:8080/auth/admin/realms/APP/users/a17e226b-8d68-4d4e-99d5-2b808b02975b/federated-identity

* TEST DE L'API SUR LE SERVEUR : http://localhost:8080/auth/realms/master/hello
