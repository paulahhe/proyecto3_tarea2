package com.project.demo.rest.product;

import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public List<Product> getAllProductos() {return productRepository.findAll();}

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producto no encontrado con ID: " + id)
        );
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
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setInStock(product.getInStock());
                    return productRepository.save(existingProduct);
                })
                .orElseGet(()-> {
                    product.setId(id);
                    return productRepository.save(product);
                });
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

}
