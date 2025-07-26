package com.alura.challenalura.dto;

import lombok.Data;

@Data
public class AuthorDTO {
    private String name;
    private Integer birth_year;
    private Integer death_year;
}