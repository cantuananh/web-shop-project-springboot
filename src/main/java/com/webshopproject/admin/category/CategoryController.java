package com.webshopproject.admin.category;

import com.webshopproject.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/categories")
    public String getListCategory(Model model){
        List<Category> listCategory = categoryService.getListCategory();
        model.addAttribute("listCategory", listCategory);
        return "category/index";
    }
}
