package com.dh.msusers.service;

import com.dh.msusers.model.User;
import com.dh.msusers.repository.KeycloakUserRepository;
import com.dh.msusers.repository.feign.BillsFeignRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final KeycloakUserRepository keycloakUserRepository;
    private final BillsFeignRepository billsFeignRepository;

    public UserService(KeycloakUserRepository keycloakUserRepository, BillsFeignRepository billsFeignRepository) {
        this.keycloakUserRepository = keycloakUserRepository;
        this.billsFeignRepository = billsFeignRepository;
    }

    public User getAllBill(String customerId) {
        User usuario = keycloakUserRepository.findById(customerId);
        usuario.setBills(billsFeignRepository.findByCustomerId(customerId));
        return usuario;
    }
}
