package com.dh.keycloak.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

@Service
public class KeycloakClientService {

    private final Keycloak keycloakAdmin;

    public KeycloakClientService(Keycloak keycloakAdmin) {
        this.keycloakAdmin = keycloakAdmin;
    }

    public void createRealmAndClient(String reign, String clientId, String clientSecret, List<String> roles) {
        // Creamos el reino
        RealmsResource realmsResource = keycloakAdmin.realms();
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(reign);
        realm.setEnabled(true);
        keycloakAdmin.realms().create(realm);
        System.out.println("Reino creado exitosamente");

        // Seteamos roles a nivel Reino
        RolesResource rolesResource = realmsResource.realm(reign).roles();
        RoleRepresentation rolAdmin = new RoleRepresentation();
        rolAdmin.setName("app_admin");
        RoleRepresentation rolUser = new RoleRepresentation();
        rolUser.setName("app_user");
        List<RoleRepresentation> rolesReino = List.of(rolAdmin, rolUser);
        for (RoleRepresentation rol : rolesReino) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(rol.getName());
            rolesResource.create(roleRepresentation);
        }

        // Verificamos que se haya creado el reino y creamos los clientes
        RealmRepresentation realmRepresentation = realmsResource.realm(reign).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, reign)) {
            RealmResource realmResource = keycloakAdmin.realm(reign);
            ClientsResource clientsResource = realmResource.clients();
            ClientsResource gwResource = realmResource.clients();

            // Creamos cliente
            ClientRepresentation usersClient = new ClientRepresentation();
            usersClient.setClientId(clientId);
            usersClient.setSecret(clientSecret);
            usersClient.setServiceAccountsEnabled(true);
            usersClient.setDirectAccessGrantsEnabled(true);
            usersClient.setEnabled(true);

            // Creamos gateway
            ClientRepresentation gatewayClient = new ClientRepresentation();
            String url = "http://localhost:8090";
            gatewayClient.setClientId("gateway-client");
            gatewayClient.setSecret("gateway-secret");
            gatewayClient.setRootUrl(url);
            gatewayClient.setWebOrigins(List.of("/*"));
            gatewayClient.setRedirectUris(List.of(url+"/*"));
            gatewayClient.setAdminUrl(url);
            gatewayClient.setEnabled(true);
            gatewayClient.setServiceAccountsEnabled(true);
            gatewayClient.setDirectAccessGrantsEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response response = clientsResource.create(usersClient);
            Response responseGW = gwResource.create(gatewayClient);
            if (response.getStatus() == 201 && responseGW.getStatus() == 201) {
                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
                String createdClientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("ClientId: " + createdClientId + ". Roles: " + roles);
                // Lo mismo pero con gw
                String createdClientIdGW = responseGW.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("GW: " + createdClientIdGW + ". Roles: " + roles);

                // Obtenemos id de clientes
                ClientResource clientResource = clientsResource.get(createdClientId);
                ClientResource clientResourceGW = gwResource.get(createdClientIdGW);
                for (String rol : roles) {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(rol);
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setContainerId(createdClientId);

                    clientResource.roles().create(roleRepresentation);
                    clientResourceGW.roles().create(roleRepresentation);
                }
                System.out.println("Recorrido completo");
            } else {
                System.out.println("Error: " + response);
            }

        }
    }

}
