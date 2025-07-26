package com.alura.challenalura.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookResponseDTO {
    private Integer count;
    private String next;
    private String previous;
    private List<BookDTO> results;
}