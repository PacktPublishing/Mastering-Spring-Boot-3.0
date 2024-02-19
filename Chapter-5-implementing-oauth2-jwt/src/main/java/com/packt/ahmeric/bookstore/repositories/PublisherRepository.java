package com.packt.ahmeric.bookstore.repositories;

import com.packt.ahmeric.bookstore.data.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

}
