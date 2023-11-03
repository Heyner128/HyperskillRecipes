package me.heyner.hyperskillrecipes.controllers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import me.heyner.hyperskillrecipes.exception.UserAlreadyRegisteredException;
import me.heyner.hyperskillrecipes.models.RecipesUser;
import me.heyner.hyperskillrecipes.service.RecipesUserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class RecipeUserController {

    private final RecipesUserService userService;

    public RecipeUserController(RecipesUserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody RecipesUser user) {
        try {
            userService.createUser(user.getEmail(), user.getPassword());
        } catch(DataIntegrityViolationException ex) {
           throw new UserAlreadyRegisteredException("The user " + user.getEmail() + " already exists");
        }
    }
}
