package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;

import com.project.demo.logic.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("No se encontró la categoría con Id" + id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Category addCategory (@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Category updateCategory(@PathVariable String id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    return categoryRepository.save(existingCategory);
                })
                .orElseGet(()-> {
                    category.setId(id);
                    return categoryRepository.save(category);
                });
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteCategory(@PathVariable String id) {
        categoryRepository.deleteById(id);
    }



}
