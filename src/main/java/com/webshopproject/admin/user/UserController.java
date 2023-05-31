package com.webshopproject.admin.user;

import com.webshopproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getListAll(Model model){
        List<User> listUsers = userService.listAll();

        model.addAttribute("listUsers", listUsers);

        return "user/index";
    }
}
