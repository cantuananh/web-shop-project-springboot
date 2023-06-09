package com.webshopproject.admin.category;

import com.webshopproject.admin.FileUploadUtil;
import com.webshopproject.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
        return listByPage(1, sortDir, model);
    }

//    @GetMapping("/categories")
//    public String getListCategory(@Param("sortDir") String sortDir, Model model) {
//        if (sortDir == null || sortDir.isEmpty()) {
//            sortDir = "asc";
//        }
//
//        List<Category> listCategory = categoryService.getListCategory(sortDir);
//
//        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
//
//        model.addAttribute("listCategory", listCategory);
//        model.addAttribute("reverseSortDir", reverseSortDir);
//
//        return "category/index";
//    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, @Param("sortDir") String sortDir, Model model) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> listCategory = categoryService.listByPage(categoryPageInfo, pageNum, sortDir);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPage", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElement());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("listCategory", listCategory);
        model.addAttribute("reverseSortDir", reverseSortDir);

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
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();
            FileUploadUtil.cleanDirectory(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        String message = "The category ID " + category.getId() + " has been saved successfully";
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryWithId(id);
            List<Category> listCategories = categoryService.getListCategoriesUsedInForm();
            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit category (ID:" + id + ")");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }

        return "category/create";
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateEnabledStatusCategory(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        categoryService.updateEnabledStatusCategory(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            String categoryDir = "/category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/categories";
    }
}
