package com.dh.msusers.repository;

import com.dh.msusers.model.User;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeycloakUserRepository {
    private final Keycloak keycloak;
    @Value("${dh.keycloak.realm}")
    private String realm;

    public User findById(String id) {
        UserRepresentation user = keycloak.realm(realm).users().get(id).toRepresentation();
        return toUser(user);
    }

    private User toUser(UserRepresentation userRepresentation) {
        return new User(userRepresentation.getId(), userRepresentation.getUsername(), userRepresentation.getEmail(), userRepresentation.getFirstName());
    }
}
