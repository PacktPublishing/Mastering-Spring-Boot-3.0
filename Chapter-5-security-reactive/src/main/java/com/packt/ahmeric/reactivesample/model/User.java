package com.packt.ahmeric.reactivesample.model;

import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(@Id  Long id, String name, String email) {

}

