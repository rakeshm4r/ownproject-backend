package com.own.project.dao;

import java.util.List;

import com.own.project.model.DynamicHeadingTags;

public interface DynamicHeadingTagsDao {

  public List<DynamicHeadingTags> saveFormFields(List<DynamicHeadingTags> fields);

  public  boolean existsByHeaderName(String headerName);

  public List<DynamicHeadingTags> getAllHeagings();

 public boolean  updateHeadingTag(Long headingId,String headerName);

 public boolean updateOrDeleteHeadingTag(Long headingId, String headerName, String status);
 
 public boolean updateHeadingTagStatusToDeleted(Long headingId);
}
