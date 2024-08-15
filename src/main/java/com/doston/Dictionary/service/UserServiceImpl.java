package com.doston.Dictionary.service;

import com.doston.Dictionary.entity.UserEntity;
import com.doston.Dictionary.repo.RoleRepository;
import com.doston.Dictionary.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements com.doston.Dictionary.service.UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository repository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
