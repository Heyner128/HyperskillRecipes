package me.heyner.hyperskillrecipes.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import me.heyner.hyperskillrecipes.adapter.RecipeUserAdapter;
import me.heyner.hyperskillrecipes.models.Recipe;
import me.heyner.hyperskillrecipes.models.RecipesUser;
import me.heyner.hyperskillrecipes.repositories.RecipeRepository;
import me.heyner.hyperskillrecipes.repositories.RecipesUserRepository;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/recipe")
@Validated
public class RecipeController {


    private final RecipeRepository repository;

    private final RecipesUserRepository userRepository;

    @Autowired
    public RecipeController(RecipeRepository repository, RecipesUserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable int id) {
        try {
            Optional<Recipe> recipe = repository.findById(id);
            return recipe.orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Recipe %d not found", id));
        }
    }


    @PostMapping("/new")
    public Map<String, Integer> addRecipe(@AuthenticationPrincipal UserDetails details, @RequestBody @Valid Recipe recipe) {
        RecipesUser author = userRepository.findByEmailIgnoreCase(details.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        recipe.setAuthor(author);
        Recipe newRecipe = repository.save(recipe);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", newRecipe.getId());
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable int id, @AuthenticationPrincipal UserDetails loggedUser){

        Recipe recipe = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        if(!loggedUser.getUsername().equals(recipe.getAuthor().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        repository.deleteById(id);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(@RequestBody @Valid Recipe recipe, @PathVariable int id, @AuthenticationPrincipal UserDetails loggedUser) {
        Recipe currentRecipe = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!loggedUser.getUsername().equals(currentRecipe.getAuthor().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        currentRecipe.setName(recipe.getName());
        currentRecipe.setDescription(recipe.getDescription());
        currentRecipe.setCategory(recipe.getCategory());
        currentRecipe.setIngredients(recipe.getIngredients());
        currentRecipe.setDirections(recipe.getDirections());

        repository.save(currentRecipe);
    }

    @GetMapping("/search")
    public List<Recipe> searchRecipe(@RequestParam Optional<String> category, @RequestParam Optional<String> name) {
        if(category.isPresent() == name.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if(category.isPresent()) {
            return repository.findByCategoryIgnoreCaseOrderByDateDesc(category.get());
        } else  {
            return repository.findByNameContainingIgnoreCaseOrderByDateDesc(name.get());
        }
    }
}
