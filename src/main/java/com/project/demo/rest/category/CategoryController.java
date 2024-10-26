package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll( // integratedNew
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString()); // Para los get all
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                categoriesPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public Category getCategoryById(@PathVariable Long id) {
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
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {

        Optional<Category> existingCategory = categoryRepository.findById(id);

        if(existingCategory.isPresent()) {
            //category.setId(existingCategory.get().getId());
            //categoryRepository.save(category);
            categoryRepository.save(existingCategory.get());
            return new GlobalResponseHandler().handleResponse("Category updated succesfully", category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id" + id + "not found", HttpStatus.NOT_FOUND, request);
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCategory (@PathVariable Long categoryId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if(existingCategory.isPresent()) {
            if(category.getName() != null) existingCategory.get().setName(category.getName());
            if(category.getDescription() != null) existingCategory.get().setDescription(category.getDescription());
            categoryRepository.save(existingCategory.get());
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    existingCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category with id " + categoryId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if(existingCategory.isPresent()) {
            categoryRepository.deleteById(existingCategory.get().getId());
            return new GlobalResponseHandler().handleResponse("Category deleted succesfully", existingCategory.get(), HttpStatus.OK, request);

        } else {
            return new GlobalResponseHandler().handleResponse("Category with id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
