package com.alura.challenalura.repository;

import com.alura.challenalura.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    long countByLanguage(String language);

    @Query("SELECT DISTINCT b.language FROM BookEntity b")
    List<String> findAllLanguages();

    List<BookEntity> findByLanguage(String language);

    boolean existsByGutendexId(Integer gutendexId);
}