package com.bibliotheque.gestion_pret.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AdherentRepository adherentRepository;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            System.out.println("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Si l'utilisateur a le rôle ADMIN
        if (authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "/admin/dashboard";
        }

        // Pour tous les autres utilisateurs authentifiés
        else {
            // On récupère l'email de l'utilisateur connecté
            String username = ((User) authentication.getPrincipal()).getUsername();
            // On cherche l'adhérent en base pour récupérer son ID
            Adherent adherent = adherentRepository.findByEmail(username).orElse(null);

            if (adherent != null) {
                // On redirige vers une page utilisateur avec son ID
                return "/user/dashboard/" + adherent.getId();
            } else {
                // Cas d'erreur peu probable mais à gérer
                return "/login?error";
            }
        }
    }
}