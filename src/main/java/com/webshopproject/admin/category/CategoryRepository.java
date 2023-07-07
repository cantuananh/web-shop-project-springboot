package com.webshopproject.admin.category;

import com.webshopproject.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("select c from Category c where c.parent.id is null")
    public List<Category> findRootCategories();
}
