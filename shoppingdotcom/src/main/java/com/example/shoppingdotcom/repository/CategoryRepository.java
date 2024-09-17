package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    public Boolean existsByName(String name);

    public List<Category> findByIsActiveTrue();
}
