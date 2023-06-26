package com.dh.keycloak;

import com.dh.keycloak.service.KeycloakService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@SpringBootApplication
public class KeycloakApplication implements CommandLineRunner {

	private static final String REALM_NAME = "EcommerceAparicio";
	private static final String USERS_CLIENT = "users-client";
	private static final String GATEWAY_CLIENT = "gateway-client";
	private static final String GATEWAY_URL = "http://localhost:8090";
	private static final String USER1_NAME = "Juliana";
	private static final String USER2_NAME = "Pia";
	private static final String USER_PASSWORD = "password";
	private static final String ROLE_NAME = "USER";
	private static final String GROUP_NAME = "PROVIDERS";



	@Autowired
	private Keycloak keycloak;

	@Autowired
	private KeycloakService keycloakService;

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Optional<RealmRepresentation> representationOptional = keycloak.realms().findAll().stream().filter(r -> r.getRealm().equals(REALM_NAME)).findAny();
		if (representationOptional.isPresent()) {
			System.out.println("Removing already pre-configured realm: " + REALM_NAME);
			keycloakService.deleteRealm(REALM_NAME);
		}

		keycloakService.createRealm(REALM_NAME);

		keycloakService.createClient(REALM_NAME, USERS_CLIENT);
		keycloakService.createGatewayClient(REALM_NAME, GATEWAY_CLIENT, GATEWAY_URL);

		keycloakService.createUser(REALM_NAME, USER1_NAME, USER_PASSWORD);
		keycloakService.createUser(REALM_NAME, USER2_NAME, USER_PASSWORD);

		keycloakService.createRole(ROLE_NAME);

		keycloakService.createGroup(REALM_NAME, GROUP_NAME);

		keycloakService.assignUserToGroup(REALM_NAME, USER1_NAME, GROUP_NAME);
		keycloakService.assignUserToGroup(REALM_NAME, USER2_NAME, GROUP_NAME);

		//keycloakClientService.createRealmAndClient("Ecommerce Aparicio", "test-client", "secret", List.of("admin", "user"));

		System.exit(0);
	}

}
