package me.heyner.hyperskillrecipes.repositories;

import org.springframework.data.repository.CrudRepository;
import me.heyner.hyperskillrecipes.models.Recipe;

import java.util.List;


public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Recipe> findByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
