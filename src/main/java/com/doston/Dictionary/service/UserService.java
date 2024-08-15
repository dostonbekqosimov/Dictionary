package com.doston.Dictionary.service;


import com.doston.Dictionary.entity.UserEntity;

public interface UserService {




    UserEntity findByUsername(String username);
}
