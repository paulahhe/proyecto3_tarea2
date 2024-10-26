package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;

import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
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
@RequestMapping ("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    /*@GetMapping
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public List<Product> getAllProductos() {return productRepository.findAll();}*/
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll( // integratedNew
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString()); // Para los get all
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                productsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producto no encontrado con ID: " + id)
        );
    }

    @GetMapping("/category/{id}/products") // integratedNew
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllByCategory (@PathVariable Long idCategoria,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(idCategoria);
        if(foundCategory.isPresent()) {


            Pageable pageable = PageRequest.of(page-1, size);
            Page<Product> productPage = productRepository.getProductByCategoryId(idCategoria, pageable);
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(productPage.getTotalPages());
            meta.setTotalElements(productPage.getTotalElements());
            meta.setPageNumber(productPage.getNumber() + 1);
            meta.setPageSize(productPage.getSize());


            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    productPage.getContent(), HttpStatus.OK, meta);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + idCategoria + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/filterByName/{name}")
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public List<Product> getProductbyId(@PathVariable String name) {
        return productRepository.findProductsWithCharacterInName(name);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    /*@PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> addProduct(@RequestBody Product product, HttpServletRequest request) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            return new GlobalResponseHandler().handleResponse("Category ID must be provided", HttpStatus.BAD_REQUEST, request);
        }

        Optional<Category> foundCategory = categoryRepository.findById(product.getCategory().getId());
        if (foundCategory.isPresent()) {
            product.setCategory(foundCategory.get());
            Product savedProduct = productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product created successfully", savedProduct, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category with ID " + product.getCategory().getId() + " not found", HttpStatus.NOT_FOUND, request);
        }
    }*/

    @PostMapping("/category/{id}")
    public ResponseEntity<?> addProductToCategory(@PathVariable Long categoryId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            product.setCategory(foundCategory.get());
            Product savedProduct = productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product created successfully",
                    savedProduct, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')") //Estaba desactivada antes y si funcionaba
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {

        Optional<Product> existingProduct = productRepository.findById(id); //Obj guarda la referencia

        if(existingProduct.isPresent()) {
            existingProduct.get().setCategory(product.getCategory());
            productRepository.save(existingProduct.get());
            return new GlobalResponseHandler().handleResponse("Product updated succesfully", product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id" + id + "not found", HttpStatus.NOT_FOUND, request);
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProduct (@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if(existingProduct.isPresent()) {
            if(product.getName() != null) existingProduct.get().setName(product.getName());
            if(product.getDescription() != null) existingProduct.get().setDescription(product.getDescription());
            if(product.getPrice() != null) existingProduct.get().setPrice(product.getPrice());
            if(product.getInStock() != null) existingProduct.get().setInStock(product.getInStock());
            productRepository.save(existingProduct.get());
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    existingProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product with id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if(existingProduct.isPresent()) {
//            Optional<Category> category = categoryRepository.findById(existingProduct.get().getCategory().getId());
//            category.get().getProducts().remove(existingProduct.get());
            productRepository.deleteById(existingProduct.get().getId());
            return new GlobalResponseHandler().handleResponse("Product deleted succesfully", existingProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id" + id + "not found", HttpStatus.NOT_FOUND, request);
        }
    }
}
