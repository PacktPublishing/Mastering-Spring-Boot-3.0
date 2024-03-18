package com.packt.ahmeric.bookstore.data;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publishers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String address;
}
