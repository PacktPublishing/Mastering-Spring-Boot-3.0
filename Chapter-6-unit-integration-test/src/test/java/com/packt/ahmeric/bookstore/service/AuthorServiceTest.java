package com.packt.ahmeric.bookstore.service;

import com.packt.ahmeric.bookstore.data.Author;
import com.packt.ahmeric.bookstore.data.Publisher;
import com.packt.ahmeric.bookstore.repositories.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author givenAuthor;
    private Author savedAuthor;

    @BeforeEach
    void setup() {
        Publisher defaultPublisher = Publisher.builder().name("Packt Publishing").build();
        givenAuthor = Author.builder()
                .name("Author Name")
                .biography("Biography of Author")
                .publisher(defaultPublisher)
                .build();
        savedAuthor = Author.builder()
                .id(1L)
                .name("Author Name")
                .publisher(defaultPublisher)
                .biography("Biography of Author")
                .build();
    }

    @Test
    void whenCreateAuthor_thenReturnSavedAuthor() {
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);
        Author actualAuthor = authorService.createAuthor(givenAuthor);
        assertAll(
                () -> assertNotNull(actualAuthor),
                () -> assertEquals(savedAuthor.getId(), actualAuthor.getId()),
                () -> assertEquals(savedAuthor.getName(), actualAuthor.getName()),
                () -> assertEquals(savedAuthor.getBiography(), actualAuthor.getBiography()),
                () -> assertEquals(savedAuthor.getPublisher(), actualAuthor.getPublisher())
        );
    }

    @Test
    void givenExistingAuthorId_whenGetAuthor_thenReturnAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(savedAuthor));
        Optional<Author> author = authorService.getAuthor(1L);
        assertTrue(author.isPresent(), "Author should be found");
        assertEquals(1L, author.get().getId(), "Author ID should match");
    }

    @Test
    void givenNonExistingAuthorId_whenGetAuthor_thenThrowAuthorNotFoundException() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Author> author = authorService.getAuthor(1L);
        assertFalse(author.isPresent());
    }

    @Test
    void whenGetAllAuthors_thenReturnAllAuthors() {
        List<Author> authors = List.of(savedAuthor);
        when(authorRepository.findAll()).thenReturn(authors);
        List<Author> result = authorService.getAllAuthors();
        assertEquals(authors, result, "Should return all authors");
    }

    @Test
    void givenValidAuthor_whenUpdateAuthor_thenReturnUpdatedAuthor() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(givenAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);
        Optional<Author> result = authorService.updateAuthor(savedAuthor.getId(), savedAuthor);
        assertTrue(result.isPresent(), "Updated author should be returned");
        result.ifPresent(author -> {
            assertEquals("Author Name", author.getName());
            assertEquals("Biography of Author", author.getBiography());
        });
    }

    @Test
    void givenExistingAuthor_whenDeleteAuthor_thenAuthorIsDeleted() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(savedAuthor));
        authorService.deleteAuthor(1L);
        verify(authorRepository).delete(savedAuthor);
    }

    @Test
    void givenNonExistingAuthor_whenDeleteAuthor_thenThrowAuthorNotFoundException() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> authorService.deleteAuthor(1L));
    }
}
