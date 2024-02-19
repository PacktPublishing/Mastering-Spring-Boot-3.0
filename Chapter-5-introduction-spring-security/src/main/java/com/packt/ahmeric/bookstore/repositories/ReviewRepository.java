package com.packt.ahmeric.bookstore.repositories;

import com.packt.ahmeric.bookstore.data.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {

}
