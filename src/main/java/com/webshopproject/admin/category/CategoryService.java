package com.webshopproject.admin.category;

import com.webshopproject.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getListCategory() {
        return (List<Category>) categoryRepository.findAll();
    }
}
