package com.own.project.dao;

import java.util.List;

import com.own.project.model.Product;
import com.own.project.model.ProductCategory;

public interface ProductDao {

  public void saveProduct(Product product) throws Exception;

  public boolean isProductNameExists(String productName);

  public boolean isProductCategoryNameExists(String categoryName);

  public ProductCategory createCategory(ProductCategory category);

  public List<ProductCategory> getAllCategories();

  public List<Product> getAllProducts();

  public boolean updateProduct(Long productId, Product updatedProductDetails);
}
