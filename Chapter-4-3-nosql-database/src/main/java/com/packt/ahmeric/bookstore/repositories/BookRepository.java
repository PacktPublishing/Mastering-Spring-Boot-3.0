package com.packt.ahmeric.bookstore.repositories;

import com.packt.ahmeric.bookstore.data.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
