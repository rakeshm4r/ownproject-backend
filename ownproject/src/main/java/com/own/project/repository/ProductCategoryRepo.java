package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.own.project.model.ProductCategory;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Long> {

  public ProductCategory findByProductCategoryName(String productCategoryName);

}
