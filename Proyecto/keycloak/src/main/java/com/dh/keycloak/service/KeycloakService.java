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

    public void createRealm(String realm) {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(realm);
        realmRepresentation.setEnabled(true);
        keycloak.realms().create(realmRepresentation);

        RealmsResource realmsResource = keycloak.realms();
        RolesResource rolesResource = realmsResource.realm(realm).roles();

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
    }

    public void deleteRealm(String realm) {
        keycloak.realm(realm).remove();
        keycloak.realm(realm);
    }

    public void createClient(String realmName, String clientId, String clientSecret) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setSecret(clientSecret);
        clientRepresentation.setServiceAccountsEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setEnabled(true);

        RealmResource realmResource = keycloak.realm(realmName);

        realmResource.clients().create(clientRepresentation);
    }

    public void createGatewayClient(String realmName, String clientId, String clientSecret, String url) {

        ClientRepresentation gatewayClient = new ClientRepresentation();
        gatewayClient.setClientId(clientId);
        gatewayClient.setSecret(clientSecret);
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

    }

    public void createUser(String realmName, String username, String password) {
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
    }

    public void createRole(String role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role);
    }

    public void createGroup(String realmName, String group) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);
        GroupsResource groupsResource = realmsResource.groups();

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(group);

        groupsResource.add(groupRepresentation);
    }

    public void assignUserToGroup(String realmName, String username, String groupName) {
        RealmResource realmsResource = keycloak.realms().realm(realmName);
        UsersResource usersResource = realmsResource.users();
        GroupsResource groupsResource = realmsResource.groups();

        UserRepresentation user = usersResource.search(username).get(0);

        GroupRepresentation group = groupsResource.groups().stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found"));

        groupsResource.group(group.getId()).members().add(user);
    }

}
