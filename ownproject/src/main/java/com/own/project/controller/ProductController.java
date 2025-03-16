package com.own.project.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.own.project.Config.JwtUtil;
import com.own.project.Resposes.ApiResponse;
import com.own.project.dao.ProductDao;
import com.own.project.dto.ProductDto;
import com.own.project.dto.ProductUserDto;
import com.own.project.model.Product;
import com.own.project.model.ProductCategory;
import com.own.project.repository.ProductCategoryRepo;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductCategoryRepo productCategoryRepo;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/product")
    public ResponseEntity<?> saveProduct(@RequestParam("productName") String productName,
            @RequestParam("productPrice") double productPrice,
            @RequestParam("noOfItems") int noOfItems,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("productImage") String productImageBase64) throws IOException {

        log.info("In ProductController saveProduct()");
        // Check if the product name already exists
        boolean exists = productDao.isProductNameExists(productName);
        if (exists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product name already exists");
        }
        // Decode the base64 image string into a byte array
        byte[] productImageBytes = decodeBase64ToBytes(productImageBase64);

        // Fetch the ProductCategory from the database using the categoryId
        Optional<ProductCategory> optionalCategory = productCategoryRepo.findById(categoryId);
        if (!optionalCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid category ID");
        }
        ProductCategory category = optionalCategory.get();

        // Create a new Product entity and associate it with the category
        Product product = new Product();
        product.setProductName(productName);
        product.setProductPrice(productPrice);
        product.setNoOfItems(noOfItems);
        product.setProductCategory(category); // Set the ProductCategory object, not just the categoryId
        product.setProductImage(productImageBytes); // Store image as byte array

        try {
            productDao.saveProduct(product); // Save the product to the database
            // return ResponseEntity.ok("Product saved successfully!"); // Return success
            // message
            return ResponseEntity.ok(new ApiResponse("Product saved successfully!"));
        } catch (Exception e) {
            log.error("Error saving product", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Return error message
        }

    }

    private byte[] decodeBase64ToBytes(String base64String) {
        String base64Data = base64String.split(",")[1];
        return Base64.getDecoder().decode(base64Data);
    }

    @GetMapping("/allProducts")
    public List<?> getAllProducts(HttpServletRequest request) {
        log.info("In ProductController getAllProducts()");
        List<Product> products = productDao.getAllProducts();
        
        String token = JwtUtil.getTokenFromRequest(request);
        String userRole = JwtUtil.getRoleFromToken(token);
        if ("ADMIN".equalsIgnoreCase(userRole)) {
            List<ProductDto> allProductData = products.stream()
                    .map(ProductDto::new) 
                    .collect(Collectors.toList());
            return allProductData;
        } else {
            
            List<ProductUserDto> allProductDataForUsers = products.stream()
                    .map(ProductUserDto::new) 
                    .collect(Collectors.toList());
            return allProductDataForUsers;
        }
    }

    

    @PostMapping("/category")
    public ResponseEntity<?> addCategory(@RequestBody ProductCategory category) {
        log.info("In ProductController addCategory()");

        // Check if category name already exists
        if (productDao.isProductCategoryNameExists(category.getProductCategoryName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category name already exists.");
        }

        // Create the new category
        ProductCategory createdCategory = productDao.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping("/categories")
    public List<ProductCategory> getCategories() {
        log.info("In ProductController getCategories()");
        return productDao.getAllCategories();
    }

    @GetMapping("/productId")
    public List<?> getProductById(@RequestParam("productId") String productName, HttpServletRequest request) {
        log.info("In ProductController getProductById()");

        List<Product> products = productDao.getAllProducts();

        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getProductName().equalsIgnoreCase(productName)) // Case-insensitive match
                .collect(Collectors.toList());

        List<ProductUserDto> allProductDataForUsers = filteredProducts.stream()
                .map(ProductUserDto::new)
                .collect(Collectors.toList());
        return allProductDataForUsers;

    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProductDetails) {
        log.info("In ProductController updateProduct()");
        boolean isUpdated =  productDao.updateProduct(productId, updatedProductDetails);

        if (isUpdated) {
            return ResponseEntity.ok(new ApiResponse("Product Details Updated Successfully "));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Update Product Details");
        }       
    }
}
