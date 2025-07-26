package com.alura.challenalura.service;

import com.alura.challenalura.dto.AuthorDTO;
import com.alura.challenalura.dto.BookDTO;
import com.alura.challenalura.model.AuthorEntity;
import com.alura.challenalura.model.BookEntity;
import com.alura.challenalura.repository.AuthorRepository;
import com.alura.challenalura.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public BookEntity saveBook(BookDTO bookDTO) {
        if (bookRepository.existsByGutendexId(bookDTO.getId())) {
            throw new RuntimeException("El libro ya está registrado");
        }

        BookEntity book = new BookEntity();
        book.setTitle(bookDTO.getTitle());
        book.setLanguage(bookDTO.getFirstLanguage());
        book.setDownloadCount(bookDTO.getDownload_count());
        book.setGutendexId(bookDTO.getId());
        book.setMediaType(bookDTO.getMedia_type());
        book.setCopyright(bookDTO.getCopyright());

        BookEntity savedBook = bookRepository.save(book);

        if (bookDTO.getAuthors() != null && !bookDTO.getAuthors().isEmpty()) {
            for (AuthorDTO authorDTO : bookDTO.getAuthors()) {
                AuthorEntity author = new AuthorEntity();
                author.setName(authorDTO.getName());
                author.setBirthYear(authorDTO.getBirth_year());
                author.setDeathYear(authorDTO.getDeath_year());
                author.setBook(savedBook);
                authorRepository.save(author);
            }
        }

        return savedBook;
    }

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<BookEntity> getBooksByLanguage(String language) {
        return bookRepository.findByLanguage(language);
    }

    public List<String> getAllLanguages() {
        return bookRepository.findAllLanguages();
    }

    public List<AuthorEntity> getAllAuthors() {
        return authorRepository.findAllWithBooks();
    }

    public List<AuthorEntity> getAuthorsAliveInYear(int year) {
        return authorRepository.findByBirthYearLessThanEqualAndDeathYearGreaterThanEqualOrDeathYearIsNullAndBirthYearLessThanEqual(
                year, year, year);
    }

    public Map<String, Long> getLanguageStatistics() {
        return bookRepository.findAllLanguages().stream()
                .filter(lang -> lang.equals("en") || lang.equals("es"))
                .collect(Collectors.toMap(
                        lang -> lang,
                        bookRepository::countByLanguage
                ));
    }

    public String getFormattedLanguageStatistics() {
        return getLanguageStatistics().entrySet().stream()
                .map(entry -> String.format("Idioma: %s - Libros: %d",
                        entry.getKey().toUpperCase(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }
}