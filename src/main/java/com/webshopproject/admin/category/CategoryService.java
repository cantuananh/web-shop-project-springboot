package com.webshopproject.admin.category;

import com.webshopproject.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {
    private static  final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listByPage(CategoryPageInfo categoryPageInfo, int pageNumber, String sortDir) {
        Sort sort = Sort.by("name");

        if (sortDir == null || sortDir.isEmpty()) {
            sort = sort.ascending();
        } else if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = categoryRepository.findRootCategories(pageable);
        List<Category> rootCategories = pageCategories.getContent();

        categoryPageInfo.setTotalElement(pageCategories.getTotalElements());
        categoryPageInfo.setTotalPages(pageCategories.getTotalPages());

        return listHierarchicalCategories(rootCategories, sortDir);
    }

//    public List<Category> getListCategory(String sortDir) {
//        Sort sort = Sort.by("name");
//
//        if (sortDir == null || sortDir.isEmpty()) {
//            sort = sort.ascending();
//        } else if (sortDir.equals("asc")) {
//            sort = sort.ascending();
//        } else if (sortDir.equals("desc")) {
//            sort = sort.descending();
//        }
//
//        List<Category> rootCategories = categoryRepository.findRootCategories(sort);
//
//        return listHierarchicalCategories(rootCategories, sortDir);
//    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                if (sortDir.equals("asc")) {
                    return category1.getName().compareTo(category2.getName());
                } else {
                    return category2.getName().compareTo(category1.getName());
                }

            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();

                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }

    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getListCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }

    public void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category getCategoryWithId(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "Duplicate category name!";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "Duplicate alias!";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != null) {
                return "Duplicate category name!";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != null) {
                return "Duplicate alias!";
            }
        }

        return "OK";
    }

    public void updateEnabledStatusCategory(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatusCategory(id, enabled);
    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not found any category with ID " + id);
        }

        categoryRepository.deleteById(id);
    }
}
