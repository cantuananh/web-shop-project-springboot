package com.webshopproject.admin.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;


    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("email") String email) {
        return userService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
