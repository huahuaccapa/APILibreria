package com.alura.challenalura.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @Column(name = "download_count")
    private Integer downloadCount;

    @Column(name = "gutendex_id", unique = true)
    private Integer gutendexId;

    @Column(name = "media_type")
    private String mediaType;

    @Column
    private Boolean copyright;

    @ElementCollection
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject")
    private List<String> subjects;

    @ElementCollection
    @CollectionTable(name = "book_bookshelves", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "bookshelf")
    private List<String> bookshelves;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuthorEntity> authors;
}