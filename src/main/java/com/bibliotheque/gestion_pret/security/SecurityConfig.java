package com.bibliotheque.gestion_pret.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Utilise l'algorithme de hachage BCrypt, le standard actuel.
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        // Ce bean contiendra notre logique de redirection personnalisée
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Autorise l'accès sans authentification aux ressources statiques et à la page
                        // de login
                        .requestMatchers("/css/**", "/js/**", "/login", "/").permitAll()
                        // Restreint l'accès aux URLs commençant par /admin au rôle ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        // Définit l'URL de notre page de login personnalisée
                        .loginPage("/login")
                        // Utilise notre handler personnalisé pour la redirection après succès
                        .successHandler(customAuthenticationSuccessHandler())
                        // Permet à tout le monde d'accéder à la page de formulaire de connexion
                        .permitAll())
                .logout(logout -> logout
                        // URL pour se déconnecter
                        .logoutUrl("/logout")
                        // Page vers laquelle rediriger après la déconnexion
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
