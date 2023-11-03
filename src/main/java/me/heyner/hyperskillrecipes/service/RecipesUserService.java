package me.heyner.hyperskillrecipes.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import me.heyner.hyperskillrecipes.adapter.RecipeUserAdapter;
import me.heyner.hyperskillrecipes.models.RecipesUser;
import me.heyner.hyperskillrecipes.repositories.RecipesUserRepository;

@Service
public class RecipesUserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final RecipesUserRepository repository;

    public RecipesUserService(PasswordEncoder passwordEncoder, RecipesUserRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }


    public void createUser(String email, String password) throws DataIntegrityViolationException {
        RecipesUser user = new RecipesUser();
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RecipesUser user = repository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new RecipeUserAdapter(user);
    }
}
