package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    public Category saveCategory(Category category);

    public Boolean existsCategory(String name);

    public List<Category> getAllCategory();

    public Boolean deleteCategory(int id);

    public Category getCategoryById(int id);

    public List<Category> getAllActiveCategory();

    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);
}
