package com.bibliotheque.gestion_pret.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdherentRepository adherentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Adherent adherent = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Adhérent non trouvé avec l'email : " + email));

        String role = adherent.getTypeAdherent().getNom().toUpperCase();

        Collection<? extends GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        return new User(adherent.getEmail(), adherent.getMotDePasse(), authorities);
    }
}