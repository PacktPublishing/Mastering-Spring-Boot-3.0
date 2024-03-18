package integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packt.ahmeric.bookstore.BookstoreApplication;
import com.packt.ahmeric.bookstore.data.Author;
import com.packt.ahmeric.bookstore.repositories.AuthorRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BookstoreApplication.class)
class AuthorControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();



    @BeforeEach
    void clearData() {
        authorRepository.deleteAll();
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testGetAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();
        authorRepository.save(author);

        mockMvc.perform(get("/authors/" + author.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(author.getName())));
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testPostAuthor() throws Exception {
        Author author = Author.builder().name("Author Name").build();


        mockMvc.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk());

        List<Author> authors = authorRepository.findAll();
        assertEquals(1, authors.size());
        assertEquals(author.getName(), authors.get(0).getName());
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testGetAllAuthors() throws Exception {

        authorRepository.save(Author.builder().name("Author Name").build());
        authorRepository.save(Author.builder().name("Author Name").build());
        authorRepository.save(Author.builder().name("Author Name").build());


        MvcResult mvcResult = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testPutAuthor() throws Exception {
        Author author = authorRepository.save(Author.builder().name("Author Name").build());
        Author updatedAuthor = Author.builder().name("Updated Author Name").build();


        mockMvc.perform(put("/authors/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedAuthor.getName())));
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testDeleteAuthorSuccess() throws Exception {
        Author author = authorRepository.save(Author.builder().name("Author Name").build());

        mockMvc.perform(delete("/authors/" + author.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="testUser", authorities={"ROLE_ADMIN"})
    void testDeleteAuthorFail() throws Exception {

        mockMvc.perform(delete("/authors/" + 1))
                .andExpect(status().isNotFound());
    }


}
