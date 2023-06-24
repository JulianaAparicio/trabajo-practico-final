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

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealmAndClient(String reino, String clientId, String clientSecret, List<String> roles) {
        // Creamos el reino
        RealmsResource realmsResource = keycloak.realms();
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(reino);
        realm.setEnabled(true);
        keycloak.realms().create(realm);
        System.out.println("Reino creado exitosamente");

        // Seteamos roles a nivel Reino
        RolesResource rolesResource = realmsResource.realm(reino).roles();
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
        RealmRepresentation realmRepresentation = realmsResource.realm(reino).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, reino)) {
            RealmResource realmResource = keycloak.realm(reino);
            ClientsResource clientsResource = realmResource.clients();
            ClientsResource gwResource = realmResource.clients();

            // Creamos cliente
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId(clientId);
            client.setSecret(clientSecret);
            client.setServiceAccountsEnabled(true);
            client.setDirectAccessGrantsEnabled(true);
            client.setEnabled(true);

            // Creamos gateway
            ClientRepresentation gateway = new ClientRepresentation();
            String url = "http://localhost:9090";
            gateway.setClientId("gateway-client");
            gateway.setSecret("gateway-secret");
            gateway.setRootUrl(url);
            gateway.setWebOrigins(List.of("/*"));
            gateway.setRedirectUris(List.of(url+"/*"));
            gateway.setAdminUrl(url);
            gateway.setEnabled(true);
            gateway.setServiceAccountsEnabled(true);
            gateway.setDirectAccessGrantsEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response response = clientsResource.create(client);
            Response responseGW = gwResource.create(gateway);
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
