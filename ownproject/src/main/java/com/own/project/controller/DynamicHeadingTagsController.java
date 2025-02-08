package com.own.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.Resposes.ApiResponse;
import com.own.project.dao.DynamicHeadingTagsDao;
import com.own.project.exception.DynamicHeadingTagsException;
import com.own.project.model.DynamicHeadingTags;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/form")
public class DynamicHeadingTagsController {

  private static final Logger log = LoggerFactory.getLogger(DynamicHeadingTagsController.class);

  @Autowired
  private DynamicHeadingTagsDao dynamicHeadingTagsDao;

  @PostMapping("/submit")
  // @PreAuthorize("hasRole('Admin')")
  public ResponseEntity<Object> submitForm(@RequestBody List<DynamicHeadingTags> fields) {
    log.info("In DynamicHeadingTagsController submitForm()");

    // List to collect all the duplicate fieldHeaders
    List<String> duplicateFieldHeaders = fields.stream()
        .filter(field -> dynamicHeadingTagsDao.existsByHeaderName(field.getHeaderName())) // Check if fieldHeader
                                                                                            // already exists
        .map(DynamicHeadingTags::getHeaderName) // Extract the fieldHeader value
        .distinct() // Avoid duplicates in the response
        .collect(Collectors.toList());

    if (!duplicateFieldHeaders.isEmpty()) {
      // If there are duplicates, return them as a list in the response
      throw new DynamicHeadingTagsException(
          "Fields with Field Headers already exist: " + String.join(", ", duplicateFieldHeaders));
    }

    // No duplicates, proceed to save the fields
    List<DynamicHeadingTags> dynamicHeadingTags = dynamicHeadingTagsDao.saveFormFields(fields);
    return ResponseEntity.ok(dynamicHeadingTags);
  }

  @GetMapping("/all-headings")
  public ResponseEntity<?> getAllHeagings() {
    log.info("In DynamicHeadingTagsController of getAllHeagings()");

    return ResponseEntity.ok(dynamicHeadingTagsDao.getAllHeagings());
  }

  @PutMapping("/updateHeadingTag")
  public ResponseEntity<?> updateHeadingTag(@RequestBody DynamicHeadingTags dynamicHeadingTags) {
    log.info("In DynamicHeadingTagsController of updateHeadingTag()");
    boolean isUpdated = dynamicHeadingTagsDao.updateHeadingTag(dynamicHeadingTags.getHeadingId(),
                                                               dynamicHeadingTags.getHeaderName());
    if (isUpdated) {
      return ResponseEntity.ok(new ApiResponse("Heading Tag updated successfully "));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Heading Tag name");
    }
  }

  @PutMapping("/updateOrDeleteHeadingTag")
  public ResponseEntity<?> updateOrDeleteHeadingTag(@RequestBody DynamicHeadingTags dynamicHeadingTags) {
      log.info("In DynamicHeadingTagsController of updateOrDeleteHeadingTag()");

      // If the status is "1", it's a delete operation, so set the status to "1" (logical delete)
      if ("1".equals(dynamicHeadingTags.getHeaderStatus())) {
          boolean isDeleted = dynamicHeadingTagsDao.updateHeadingTagStatusToDeleted(dynamicHeadingTags.getHeadingId());
          if (isDeleted) {
              return ResponseEntity.ok(new ApiResponse("Heading Tag marked as deleted successfully"));
          } else {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Heading Tag");
          }
      }

      // Otherwise, it's an update operation
      boolean isUpdated = dynamicHeadingTagsDao.updateOrDeleteHeadingTag(dynamicHeadingTags.getHeadingId(),
                                                                dynamicHeadingTags.getHeaderName(),
                                                                dynamicHeadingTags.getHeaderStatus());

      if (isUpdated) {
          return ResponseEntity.ok(new ApiResponse("Heading Tag updated successfully"));
      } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Heading Tag name");
      }
  }


}
