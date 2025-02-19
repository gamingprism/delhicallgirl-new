package com.blogs.blogs.repo;

import com.blogs.blogs.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Predefined categories can be initialized via data.sql
    Optional<Category> findByNameEqualsIgnoreCase(String name);
}
