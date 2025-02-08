package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.own.project.model.DynamicHeadingTags;

@Repository
public interface DynamicHeadingTagsRepo extends JpaRepository<DynamicHeadingTags, Long> {

  boolean existsByHeaderName(String headerName);

}
