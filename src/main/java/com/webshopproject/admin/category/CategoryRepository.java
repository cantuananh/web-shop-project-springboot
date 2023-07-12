package com.webshopproject.admin.category;

import com.webshopproject.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CustomPagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends CustomPagingAndSortingRepository<Category, Integer> {
    @Query("select c from Category c where c.parent.id is null")
    public List<Category> findRootCategories(Sort sort);

    @Query("select c from Category c where c.parent.id is null")
    public Page<Category> findRootCategories(Pageable pageable);

    public Category findByName(String name);

    public Category findByAlias(String alias);

    public Long countById(Integer id);

    @Query("update Category category set category.enabled = ?2 where category.id = ?1")
    @Modifying
    public void updateEnabledStatusCategory(Integer id, boolean enabled);
}
