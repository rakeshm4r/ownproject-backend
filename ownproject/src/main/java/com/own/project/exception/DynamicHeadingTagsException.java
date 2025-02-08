package com.own.project.exception;

public class DynamicHeadingTagsException extends RuntimeException {

  public DynamicHeadingTagsException(String message) {
    super(message);
  }

  public DynamicHeadingTagsException(String message, Throwable cause) {
    super(message, cause);
  }
}
