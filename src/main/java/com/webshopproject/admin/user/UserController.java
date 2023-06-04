package com.webshopproject.admin.user;

import com.webshopproject.entity.Role;
import com.webshopproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getListAll(Model model) {
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);

        return "user/index";
    }

    @GetMapping("/users/new")
    public String createNewUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create new User ");

        return "user/create";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        System.out.println(user);
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.getUserWith(id);
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
            model.addAttribute("listRoles", listRoles);
            return "user/create";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Can not found user with id: " + id);
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUserWith(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserWith(id);
            redirectAttributes.addFlashAttribute("message", "Delete user successfully");
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", "Could not found user with ID: " + id);
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }
}
