package com.alura.challenalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // Esta línea ignora campos no mapeados
public class BookDTO {
    private Integer id;
    private String title;
    private List<AuthorDTO> authors;
    private List<String> languages;
    private Integer download_count;
    private String media_type;
    private Boolean copyright;
    private List<String> bookshelves;
    private List<String> subjects;
    private List<String> summaries;  // Campo añadido
    private List<String> translators;  // Campo añadido

    public String getFirstLanguage() {
        return languages != null && !languages.isEmpty() ? languages.get(0) : "Desconocido";
    }
}