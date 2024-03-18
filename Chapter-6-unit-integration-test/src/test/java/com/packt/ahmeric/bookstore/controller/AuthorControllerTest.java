package com.packt.ahmeric.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packt.ahmeric.bookstore.config.SecurityConfig;
import com.packt.ahmeric.bookstore.data.Author;
import com.packt.ahmeric.bookstore.service.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfig.class)
class AuthorControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideRolesAndExpectedStatusForAddAuthor")
    void testAddAuthorWithDifferentRoles(String role, HttpStatus expectedStatus) throws Exception {
        Author author = new Author();
        author.setName("Author Name");

        when(authorService.createAuthor(any(Author.class))).thenReturn(author);

        mockMvc.perform(post("/authors")
                        .with(user("testUser").roles(role.replace("ROLE_", "")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andDo(print())
                .andExpect(status().is(expectedStatus.value()));
    }

    private static Stream<Arguments> provideRolesAndExpectedStatusForAddAuthor() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", HttpStatus.OK),
                Arguments.of("ROLE_USER", HttpStatus.FORBIDDEN)
        );
    }

    private static Stream<Arguments> provideRolesAndExpectedStatus() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", HttpStatus.OK),
                Arguments.of("ROLE_USER", HttpStatus.OK)
        );
    }


    @ParameterizedTest
    @MethodSource("provideRolesAndExpectedStatus")
    void testGetAuthor(String role, HttpStatus expectedStatus) throws Exception {
        Long id = 1L;
        Author author = new Author();
        author.setId(id);

        when(authorService.getAuthor(id)).thenReturn(Optional.of(author));
        mockMvc.perform(get("/authors/" + id)
                        .with(user("testUser").roles(role.replace("ROLE_", ""))))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect((result) -> {
                    Author actual = objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
                    assertEquals(author.getId(), actual.getId());
                });
    }


    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_USER", "ROLE_ADMIN"})
    void testGetAllAuthors() throws Exception {
        List<Author> authors = Arrays.asList(new Author(), new Author()); // Assuming Author has a no-args constructor

        when(authorService.getAllAuthors()).thenReturn(authors);

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(authors.size())));
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testUpdateAuthorWithAdminRole() throws Exception {
        Long id = 1L;
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Updated Name");

        when(authorService.updateAuthor(eq(id), any(Author.class))).thenReturn(Optional.of(updatedAuthor));

        mockMvc.perform(put("/authors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedAuthor.getName())));
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_USER"})
    void testUpdateAuthorWithUserRole() throws Exception {
        Long id = 1L;
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Updated Name");

        when(authorService.updateAuthor(eq(id), any(Author.class))).thenReturn(Optional.of(updatedAuthor));

        mockMvc.perform(put("/authors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedAuthor.getName())));
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testDeleteAuthorWithAdminRole() throws Exception {
        Long id = 1L;
        doNothing().when(authorService).deleteAuthor(id);

        mockMvc.perform(delete("/authors/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testDeleteAuthorNotFoundWithAdminRole() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Author not found with id: " + id))
                .when(authorService).deleteAuthor(id);

        mockMvc.perform(delete("/authors/" + id))
                .andExpect(status().isNotFound());
    }

}
