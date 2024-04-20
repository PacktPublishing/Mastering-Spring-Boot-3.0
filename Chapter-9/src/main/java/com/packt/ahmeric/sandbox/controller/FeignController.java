package com.packt.ahmeric.sandbox.controller;

import com.packt.ahmeric.sandbox.feign.JsonPlaceholderClient;
import com.packt.ahmeric.sandbox.feign.Post;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    private final JsonPlaceholderClient jsonPlaceholderClient;

    public FeignController(JsonPlaceholderClient jsonPlaceholderClient) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
    }

    @GetMapping("/feign/posts/{id}")
    public Post getPostById(@PathVariable Long id) {
        System.out.println("yaz biseyler");
        return jsonPlaceholderClient.getPostById(id);
    }

    @GetMapping("/feign/posts")
    public List<Post> getAllPosts() {
        return jsonPlaceholderClient.getPosts();
    }
}
