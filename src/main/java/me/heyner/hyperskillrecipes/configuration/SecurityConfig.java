package me.heyner.hyperskillrecipes.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers(HttpMethod.POST, "/api/register").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/recipe/*").authenticated()
                        .mvcMatchers(HttpMethod.POST, "/api/recipe/new").authenticated()
                        .mvcMatchers(HttpMethod.DELETE, "/api/recipe/*").authenticated()
                        .mvcMatchers(HttpMethod.PUT, "/api/recipe/*").authenticated()
                        .anyRequest().denyAll()
                )
                .csrf().disable()
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
