package com.own.project.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.own.project.dao.ProductDao;
import com.own.project.model.Product;
import com.own.project.model.ProductCategory;
import com.own.project.repository.ProductCategoryRepo;
import com.own.project.repository.ProductRepo;

@Service
public class ProductImpl implements ProductDao {

  @Autowired
  private ProductRepo productRepo;

  @Autowired
  private ProductCategoryRepo productCategoryRepo;

  private static final Logger log = LoggerFactory.getLogger(ProductImpl.class);

  public boolean isProductNameExists(String productName) {
    log.info("In ProductImpl isProductNameExists()");
    return productRepo.findByProductName(productName)!=null;
  }
  public boolean isProductCategoryNameExists(String productName) {
    log.info("In ProductImpl isProductCategoryNameExists()");
    return productCategoryRepo.findByProductCategoryName(productName)!=null ;
  }

  public void saveProduct(Product product)  {
    log.info("In ProductImpl saveProduct()");
    productRepo.save(product);
  }

  public ProductCategory createCategory(ProductCategory category) {
    log.info("In ProductImpl createCategory()");
    return productCategoryRepo.save(category);
  }

  public List<ProductCategory> getAllCategories() {
    log.info("In ProductImpl getAllCategories()");
    return productCategoryRepo.findAll();
  }

 
  public List<Product> getAllProducts() {
    log.info("In ProductImpl getAllProducts()");
    return productRepo.findAll();
  }


}
