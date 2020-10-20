package api;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import api.User;

@Controller
@SpringBootApplication
public class API
{
    private Keycloak keycloak = null;
    private AccessTokenResponse token = null;
    private String serverUrl = "http://localhost:8080/auth";
    private String realm = "master";
    private String username = "admin";
    private String password = "admin";
    private String clientId = "admin-cli";

    private String currentRealmApp = null;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    @ResponseBody
    String getToken()
    {
        init();

        return Tokenizer();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    String getUserByEmail(@RequestParam("email") String e)
    {
        init();
        List<UserRepresentation> users = this.keycloak.realm(this.currentRealmApp).users().list();
        for(UserRepresentation user : users) {
            if(user.getEmail().equals(e)) {
                return "[{ " +
                        "\"email\":\"" + user.getEmail() + "\"," +
                        "\"message\":\"User " + user.getUsername() + " found\"," +
                        "\"url\":\"" + this.serverUrl + "/realms/" + this.currentRealmApp + "/broker/" + "IDP1"/*TODO*/ + "/link?client_id=" + this.token + "\"," +
                        "\"code\":\"success\"" +
                        "}]";
            }
        }
        return "[{ " +
                "\"email\":\"\"," +
                "\"message\":\"User not found, please contact an administrator.\"," +
                "\"code\":\"failed\"" +
                "}]";
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseBody
    String checkCredentials(@RequestBody User user)
    {
        init();
        //TODO: CHECK CREDENTIALS IN KEYCLOAK WITH THIS USER "user"
        return "No user found with this email, please contact an administrator.";
    }

    private String getRealmApp() {
        //http://localhost:8081/auth/realms/APP/protocol/openid-connect/auth
        ServletUriComponentsBuilder url = ServletUriComponentsBuilder.fromCurrentRequestUri();
        String tmp[] = url.toUriString().split("/");
        return tmp[getIndexOf(tmp, "realms") + 1];
    }

    private int getIndexOf(String[] array, String toFind) {
        //Search a string in array
        //Return int : index or -1 if not found
        int index = -1;
        int i = 0;
        for(String s : array) {
            if(array[i].equals(toFind)) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    private String Tokenizer() {
        return this.token.getToken();
        //return "[{ \"access_token\": \"" + this.token.getToken() + "\" }]";
    }

    private void init() {
        //Get initial instance
        this.keycloak = Keycloak.getInstance(this.serverUrl, this.realm, this.username, this.password, this.clientId);
        this.token = this.keycloak.tokenManager().getAccessToken();
        this.currentRealmApp = getRealmApp();
        test();
    }

    private void showIdpFromRealmApp() {
        for (IdentityProviderRepresentation idp : this.keycloak.realm(this.currentRealmApp).identityProviders().findAll()) {
            System.out.println(this.currentRealmApp + " is connected to " + idp.getAlias()); //RETOURNE IDP1 MAIS N'EST PAS RELIÉ A l'USER
            System.out.println("Provider id: " + idp.getProviderId());
        }
    }
    private void showUsers(){
        List<UserRepresentation> users = this.keycloak.realm(this.currentRealmApp).users().list();
        for(UserRepresentation u : users) {
            System.out.println("##########################");
            System.out.println("User: " + u.getUsername());
            System.out.println("\tID: " + u.getId());
            System.out.println("\tEmail: " + u.getEmail());
            System.out.println("\tServiceAccountClientID: " + u.getServiceAccountClientId());
            System.out.println("\tFederationLink: " + u.getFederationLink());
            System.out.println("\tFirstname: " + u.getFirstName());
            System.out.println("\tLastname: " + u.getLastName());
            System.out.println("\tgetOrigin: " + u.getOrigin());
            System.out.println("\tgetSelf: " + u.getSelf());
            System.out.println("\tSOCIALS LINKS :");
            if(u.getSocialLinks() != null) {
                for(SocialLinkRepresentation social : u.getSocialLinks()) {
                    if(social != null) {
                        System.out.println("\t\tLink: " + social.getSocialUsername());
                        System.out.println("\t\t\tID: " + social.getSocialUserId());
                        System.out.println("\t\t\tPROVIDER: " + social.getSocialProvider());
                    }
                }
            }

            System.out.println("\tGROUPS :");
            if(u.getGroups() != null) {
                for (String g : u.getGroups()) {
                    if (g != null) {
                        System.out.println("\t\tGroup: " + g);
                    }
                }
            }
            System.out.println("\tCREDENTIALS :");
            if(u.getCredentials() != null) {
                for (CredentialRepresentation c : u.getCredentials()) {
                    if (c != null) {
                        System.out.println("\t\tCredential: " + c.getUserLabel());
                        System.out.println("\t\t\tID: " + c.getId());
                        System.out.println("\t\t\tDATA: " + c.getCredentialData());
                        System.out.println("\t\t\tSecret DATA: " + c.getSecretData());
                        System.out.println("\t\t\tTYPE: " + c.getType());
                        System.out.println("\t\t\tVALUE: " + c.getValue());
                        System.out.println("\t\t\tPRIORITY: " + c.getPriority());
                    }
                }
            }
            System.out.println("\tFEDERATES IDENTITIES :");
            if(u.getFederatedIdentities() != null) {
                for (FederatedIdentityRepresentation f : u.getFederatedIdentities()) {
                    if (f != null) {
                        System.out.println("\t\tIdentityProvider: " + f.getIdentityProvider());
                        System.out.println("\t\t\tID User: " + f.getUserId());
                        System.out.println("\t\t\tUsername: " + f.getUserName());
                    }
                }
            }
            System.out.println("##########################\n");
        }
    }
    private void showKeycloakInstance() {
        RealmResource tmp = this.keycloak.realm(this.currentRealmApp);
        //https://www.keycloak.org/docs-api/11.0/javadocs/org/keycloak/storage/jpa/entity/BrokerLinkEntity.html
        //https://mvnrepository.com/artifact/org.keycloak
        //https://github.com/keycloak/keycloak

    }
    private void test() {
        System.out.println("TEST START");

        showIdpFromRealmApp();
        showUsers();
        showKeycloakInstance();

        System.out.println("TEST END");
    }

    /*private void generateUrl(){
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken token = session.getToken();
        String clientId = token.getIssuedFor();
        String nonce = UUID.randomUUID().toString();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String input = nonce + token.getSessionState() + clientId + provider;
        byte[] check = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String hash = Base64Url.encode(check);
        request.getSession().setAttribute("hash", hash);
        String redirectUri = "http://localhost:8080/auth/realms/APP/broker/IDP1/endpoint"; //TODO: CHANGE
        String accountLinkUrl = KeycloakUriBuilder.fromUri(authServerRootUrl)
                .path("/auth/realms/{realm}/broker/{provider}/link")
                .queryParam("nonce", nonce)
                .queryParam("hash", hash)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri).build(realm, provider).toString();
    }
    */
    public static void main(String[] args)
    {
        SpringApplication.run(API.class, args);
    }
}
