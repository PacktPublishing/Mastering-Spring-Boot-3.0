package com.packt.ahmeric.bookstore.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "books")
@Data
public class Book {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String isbn;
}
