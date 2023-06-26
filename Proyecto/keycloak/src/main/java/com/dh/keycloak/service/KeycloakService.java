package com.dh.keycloak.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakService {

    private final Keycloak keycloak;

    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

//    public void createRealmAndClient(String reign, String clientId, String clientSecret, List<String> roles) {
//        // Creamos el reino:
//        RealmRepresentation realm = new RealmRepresentation();
//        realm.setRealm(reign);
//        realm.setEnabled(true);
//        keycloakAdmin.realms().create(realm);
//        System.out.println("Reino creado exitosamente");
//
//        RealmsResource realmsResource = keycloakAdmin.realms();
//
//        // Seteamos roles a nivel Reino
//        RolesResource rolesResource = realmsResource.realm(reign).roles();
//        RoleRepresentation rolAdmin = new RoleRepresentation();
//        rolAdmin.setName("app_admin");
//        RoleRepresentation rolUser = new RoleRepresentation();
//        rolUser.setName("app_user");
//        List<RoleRepresentation> rolesReino = List.of(rolAdmin, rolUser);
//        for (RoleRepresentation rol : rolesReino) {
//            RoleRepresentation roleRepresentation = new RoleRepresentation();
//            roleRepresentation.setName(rol.getName());
//            rolesResource.create(roleRepresentation);
//        }
//
//        // Verificamos que se haya creado el reino y creamos los clientes
//        RealmRepresentation realmRepresentation = realmsResource.realm(reign).toRepresentation();
//        String realmName = realmRepresentation.getRealm();
//        if(Objects.equals(realmName, reign)) {
//            RealmResource realmResource = keycloakAdmin.realm(reign);
//            ClientsResource clientsResource = realmResource.clients();
//            ClientsResource gwResource = realmResource.clients();
//
//            // Creamos cliente
//            ClientRepresentation usersClient = new ClientRepresentation();
//            usersClient.setClientId(clientId);
//            usersClient.setSecret(clientSecret);
//            usersClient.setServiceAccountsEnabled(true);
//            usersClient.setDirectAccessGrantsEnabled(true);
//            usersClient.setEnabled(true);
//
//            // Creamos gateway
//            ClientRepresentation gatewayClient = new ClientRepresentation();
//            String url = "http://localhost:8090";
//            gatewayClient.setClientId("gateway-client");
//            gatewayClient.setSecret("gateway-secret");
//            gatewayClient.setRootUrl(url);
//            gatewayClient.setWebOrigins(List.of("/*"));
//            gatewayClient.setRedirectUris(List.of(url+"/*"));
//            gatewayClient.setAdminUrl(url);
//            gatewayClient.setEnabled(true);
//            gatewayClient.setServiceAccountsEnabled(true);
//            gatewayClient.setDirectAccessGrantsEnabled(true);
//
//            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
//            Response response = clientsResource.create(usersClient);
//            Response responseGW = gwResource.create(gatewayClient);
//            if (response.getStatus() == 201 && responseGW.getStatus() == 201) {
//                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
//                String createdClientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//                System.out.println("ClientId: " + createdClientId + ". Roles: " + roles);
//                // Lo mismo pero con gw
//                String createdClientIdGW = responseGW.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//                System.out.println("GW: " + createdClientIdGW + ". Roles: " + roles);
//
//                // Obtenemos id de clientes
//                ClientResource clientResource = clientsResource.get(createdClientId);
//                ClientResource clientResourceGW = gwResource.get(createdClientIdGW);
//                for (String rol : roles) {
//                    RoleRepresentation roleRepresentation = new RoleRepresentation();
//                    roleRepresentation.setName(rol);
//                    roleRepresentation.setClientRole(true);
//                    roleRepresentation.setContainerId(createdClientId);
//
//                    clientResource.roles().create(roleRepresentation);
//                    clientResourceGW.roles().create(roleRepresentation);
//                }
//                System.out.println("Recorrido completo");
//            } else {
//                System.out.println("Error: " + response);
//            }
//
//        }
//    }

    public void createRealm(String realm) {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(realm);
        realmRepresentation.setEnabled(true);
        keycloak.realms().create(realmRepresentation);

        System.out.println("Reino creado exitosamente");

        RealmsResource realmsResource = keycloak.realms();
    }

    public void deleteRealm(String realm) {
        keycloak.realm(realm).remove();
        keycloak.realm(realm);
        System.out.println("Reino eliminado exitosamente");
    }

    public ClientRepresentation createClient(String realmName, String clientId) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);

        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setSecret("client-secret");
        clientRepresentation.setStandardFlowEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setServiceAccountsEnabled(true);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setRedirectUris(Collections.singletonList("*"));

        realmsResource.clients().create(clientRepresentation);

        System.out.println("Cliente creado exitosamente");

        return clientRepresentation;
    }

    public ClientRepresentation createGatewayClient(String realmName, String clientId, String url) {

        ClientRepresentation gatewayClient = new ClientRepresentation();
        gatewayClient.setClientId(clientId);
        gatewayClient.setSecret("1Zo0UgE8RYQbcnZpQ40zPoDLJ1cpGxqQ");
        gatewayClient.setRootUrl(url);
        gatewayClient.setWebOrigins(List.of("/*"));
        gatewayClient.setRedirectUris(List.of(url + "/*"));
        gatewayClient.setAdminUrl(url);
        gatewayClient.setEnabled(true);
        gatewayClient.setServiceAccountsEnabled(true);
        gatewayClient.setDirectAccessGrantsEnabled(true);
        gatewayClient.setStandardFlowEnabled(true);
        RealmResource realmsResource = keycloak.realms().realm(realmName);

        realmsResource.clients().create(gatewayClient);

        System.out.println("Cliente Gateway creado exitosamente");

        return gatewayClient;
    }

    public UserRepresentation createUser(String realmName, String username, String password) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);
        UsersResource usersResource = realmsResource.users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        userRepresentation.setCredentials(Collections.singletonList(credential));

        usersResource.create(userRepresentation);

        System.out.println("Usuario creado exitosamente");

        return userRepresentation;
    }

    public RoleRepresentation createRole(String role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role);

        System.out.println("Rol creado exitosamente");

        return roleRepresentation;
    }

    public GroupRepresentation createGroup(String realmName, String group) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);
        GroupsResource groupsResource = realmsResource.groups();

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(group);

        groupsResource.add(groupRepresentation);

        System.out.println("Grupo creado exitosamente");

        return groupRepresentation;
    }

    public void assignUserToGroup(String realmName, String username, String groupName) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);
        UsersResource usersResource = realmsResource.users();
        GroupsResource groupsResource = realmsResource.groups();

        UserRepresentation user = usersResource.search(username).get(0);
        String userId = user.getId();

        GroupRepresentation group = groupsResource.groups().stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found"));

        groupsResource.group(group.getId()).members().add(user);
    }

}
