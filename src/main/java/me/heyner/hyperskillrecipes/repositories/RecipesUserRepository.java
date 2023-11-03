package me.heyner.hyperskillrecipes.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import me.heyner.hyperskillrecipes.models.RecipesUser;

import java.util.Optional;

public interface RecipesUserRepository extends CrudRepository<RecipesUser, Long> {
    Optional<RecipesUser> findByEmailIgnoreCase(@Nullable String email);
}