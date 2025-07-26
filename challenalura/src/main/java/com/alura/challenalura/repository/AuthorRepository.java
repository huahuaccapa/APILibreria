package com.alura.challenalura.repository;

import com.alura.challenalura.model.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    List<AuthorEntity> findByBirthYearLessThanEqualAndDeathYearGreaterThanEqualOrDeathYearIsNullAndBirthYearLessThanEqual(
            Integer birthYear, Integer deathYear, Integer birthYear2);

    @Query("SELECT DISTINCT a FROM AuthorEntity a JOIN FETCH a.book")
    List<AuthorEntity> findAllWithBooks();
}