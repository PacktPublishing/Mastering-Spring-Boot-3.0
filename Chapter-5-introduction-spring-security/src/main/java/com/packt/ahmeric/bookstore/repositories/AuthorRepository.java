package com.packt.ahmeric.bookstore.repositories;

import com.packt.ahmeric.bookstore.data.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
