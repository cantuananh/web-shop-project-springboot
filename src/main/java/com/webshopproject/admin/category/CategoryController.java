package com.webshopproject.admin.category;

import com.webshopproject.admin.FileUploadUtil;
import com.webshopproject.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String getListCategory(Model model) {
        List<Category> listCategory = categoryService.getListCategory();
        model.addAttribute("listCategory", listCategory);
        return "category/index";
    }

    @GetMapping("/categories/new")
    public String createNewCategory(Model model) {
        List<Category> listCategories = categoryService.getListCategoriesUsedInForm();
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create new category");

        return "category/create";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        category.setImage(fileName);

        Category savedCategory = categoryService.save(category);

        String uploadDir = "category-images/" + savedCategory.getId();
        FileUploadUtil.cleanDirectory(uploadDir);

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        String message = "The category ID " + category.getId() + " has been saved successfully";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }
}