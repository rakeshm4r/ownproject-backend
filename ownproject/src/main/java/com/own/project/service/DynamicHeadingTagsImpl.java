package com.own.project.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.own.project.dao.DynamicHeadingTagsDao;
import com.own.project.model.DynamicHeadingTags;
import com.own.project.repository.DynamicHeadingTagsRepo;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DynamicHeadingTagsImpl implements DynamicHeadingTagsDao {

  private static final Logger log = LoggerFactory.getLogger(DynamicHeadingTagsImpl.class);

  @Autowired
  private DynamicHeadingTagsRepo dynamicHeadingTagsRepo;

 
  @Transactional
  public List<DynamicHeadingTags> saveFormFields(List<DynamicHeadingTags> fields) {
      log.info("In DynamicHeadingTagsImpl of saveFormFields()");

      // Mark as saved
      for (DynamicHeadingTags field : fields) {
          field.setHeaderStatus("1");
      }

      return dynamicHeadingTagsRepo.saveAll(fields);
  }


  @Override
  public boolean existsByHeaderName(String headerName) {
    log.info("In DynamicHeadingTagsImpl of existsByFieldHeader()");
    return dynamicHeadingTagsRepo.existsByHeaderName(headerName);
  }

  @Override
  public List<DynamicHeadingTags> getAllHeagings() {
    log.info("In DynamicHeadingTagsImpl of getAllHeagings()");
    return dynamicHeadingTagsRepo.findAll();
  }
  // @Override
  // public List<DynamicHeadingTags> getAllHeagings() {
  //     log.info("In DynamicHeadingTagsImpl of getAllHeagings()");
  //     List<DynamicHeadingTags> allTags = dynamicHeadingTagsRepo.findAll();
      
  //     // Assuming headerStatus is a String, compare it with "0" (the string value).
  //     return allTags.stream()
  //                   .filter(tag -> "0".equals(tag.getHeaderStatus()))  // Compare with "0" as a String
  //                   .collect(Collectors.toList());
  // }
  

  @Override
  public boolean updateHeadingTag(Long headingId, String headerName) {
    log.info("In DynamicHeadingTagsImpl of updateHeadingTag()");
    DynamicHeadingTags feildname = dynamicHeadingTagsRepo.findById(headingId).orElse(null);
    if (feildname != null) {
      feildname.setHeaderName(headerName);
      dynamicHeadingTagsRepo.save(feildname);
      return true;
    }
    return false;
  }

  @Override
  public boolean updateOrDeleteHeadingTag(Long headingId, String headerName, String status) {
    log.info("In DynamicHeadingTagsImpl of updateOrDeleteHeadingTag()");

    DynamicHeadingTags fieldName = dynamicHeadingTagsRepo.findById(headingId).orElse(null);

    if (fieldName != null) {
      fieldName.setHeaderName(headerName);
      fieldName.setHeaderStatus(status != null ? status : fieldName.getHeaderStatus()); // Set status if provided
      dynamicHeadingTagsRepo.save(fieldName);
      return true;
    }

    return false;
  }

  @Override
  public boolean updateHeadingTagStatusToDeleted(Long headingId) {
    log.info("In DynamicHeadingTagsImpl of updateHeadingTagStatusToDeleted()");

    DynamicHeadingTags fieldName = dynamicHeadingTagsRepo.findById(headingId).orElse(null);

    if (fieldName != null) {
      fieldName.setHeaderStatus("1"); // Mark as deleted
      dynamicHeadingTagsRepo.save(fieldName);
      return true;
    }
    return false;
  }

}
