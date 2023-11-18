package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.CategoryDAO;
import dev.alphacentaurii.RETROWARE.model.Category;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

    private JdbcTemplate jdbcTemplate;

    private final String ALL_CATEGORIES = "SELECT * FROM CATEGORIES";
    private final String CATEGORY_BY_ID = "SELECT * FROM CATEGORIES WHERE ID = ?";

    @Autowired
    public CategoryDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> getAllCategories() {
        return jdbcTemplate.query(ALL_CATEGORIES, Category::rowMap);
    }

    @Override
    public Category getCategoryById(int id) {
        Category result;

        try{
            result = jdbcTemplate.queryForObject(CATEGORY_BY_ID, Category::rowMap, id, Category.class);
            return result;
        }catch(IncorrectResultSetColumnCountException e){
            if(e.getActualCount() > 1)
                e.printStackTrace();

            return null;
        }

    }
    
}
