package dev.alphacentaurii.RETROWARE.dao;

import java.util.List;

import dev.alphacentaurii.RETROWARE.model.Category;

public interface CategoryDAO {
    
    public List<Category> getAllCategories();

    public Category getCategoryById(int id);

}
