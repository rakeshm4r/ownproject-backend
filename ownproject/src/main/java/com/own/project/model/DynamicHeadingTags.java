package com.own.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DynamicHeadingTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long headingId;

    @Column(unique = true)
    private String headerName;

    private String headerStatus;
}
