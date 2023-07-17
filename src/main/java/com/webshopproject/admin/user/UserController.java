package com.webshopproject.admin.user;

import com.webshopproject.admin.FileUploadUtil;
import com.webshopproject.entity.Role;
import com.webshopproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "firstName", "asc", null);
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listByPage(
            @PathVariable(name = "pageNumber") int pageNumber,
            Model model,
            @Param("sortField") String sortField,
            @Param("sortDir") String sortDir,
            @Param("keyword") String keyword) {
        System.out.println("Sort Field: " + sortField);
        System.out.println("Sort dir: " + sortDir);

        Page<User> page = userService.listByPage(pageNumber, sortField, sortDir, keyword);

        List<User> listUsers = page.getContent();
        long startCount = (pageNumber - 1) * UserService.USER_PER_PAGE + 1;
        long endCount = startCount * UserService.USER_PER_PAGE;
        if (startCount >= page.getTotalElements()) {
            startCount = page.getTotalElements();
        }
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        String revertSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("startCount", startCount);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("revertSortDir", revertSortDir);
        model.addAttribute("keyword", keyword);

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
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {


        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDirectory = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDirectory(uploadDirectory);
            FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return getRedirectURLtoAffectedUser(user);
    }

    private static String getRedirectURLtoAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];

        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
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
