package com.webshopproject.admin.user;

import com.webshopproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;


    public List<User> listAll() {
        return (List<User>) repository.findAll();
    }

}
