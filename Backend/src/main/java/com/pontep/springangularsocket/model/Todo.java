package com.pontep.springangularsocket.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private boolean isCompleted;
}
