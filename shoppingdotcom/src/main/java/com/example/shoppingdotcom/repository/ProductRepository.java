package com.example.shoppingdotcom.repository;

import com.example.shoppingdotcom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIsActiveTrue();

    List<Product> findByCategory(String category);

    List<Product> findByIsActiveTrueAndCategory(String category);

    List<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(String keyword, String keyword1);

    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String category);

    Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String category, Pageable pageable);

    Page<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(String title,
                                                                                                              String category, Pageable pageable);

    Page<Product> findByCategory(Pageable pageable, String category);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByIsActiveTrueAndCategory(String category, Pageable pageable);
}
