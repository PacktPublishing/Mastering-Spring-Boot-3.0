package com.packt.ahmeric.bookstore.controller;

import com.packt.ahmeric.bookstore.data.Publisher;
import com.packt.ahmeric.bookstore.repositories.PublisherRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherRepository publisherRepository;

    @PostMapping
    public ResponseEntity<Publisher> addBook(@RequestBody Publisher publisher) {
        Publisher savedPublisher = publisherRepository.save(publisher);
        return ResponseEntity.ok(savedPublisher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getBook(@PathVariable Long id) {
        Optional<Publisher> publisher = publisherRepository.findById(id);
        return publisher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        return publisherRepository.findById(id)
                .map(existingPublisher -> {
                    existingPublisher.setName(publisher.getName());
                    existingPublisher.setAddress(publisher.getAddress());
                    Publisher updatedPublisher = publisherRepository.save(existingPublisher);
                    return ResponseEntity.ok(updatedPublisher);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        if (publisherRepository.existsById(id)) {
            publisherRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
