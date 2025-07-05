package com.bibliotheque.gestion_pret.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.service.PretService;
import com.bibliotheque.gestion_pret.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PretService pretService;

    @Autowired
    private AdherentRepository adherentRepository;

    // Affiche le catalogue de livres
    @GetMapping("/dashboard/{id}")
    public String userDashboard(@PathVariable Long id, Model model) {
        List<Livre> livres = userService.getCatalogueLivres();
        model.addAttribute("livres", livres);
        model.addAttribute("userId", id);
        return "user/user-dashboard";
    }

    @PostMapping("/emprunter/{livreId}")
    public String emprunterLivre(@PathVariable Long livreId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // On trouve le vrai adhérent connecté
        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé dans la session"));

        try {
            pretService.emprunterLivre(adherent.getId(), livreId);
            redirectAttributes.addFlashAttribute("successMessage", "Livre emprunté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Redirection dynamique vers le bon dashboard
        return "redirect:/user/user-dashboard/" + adherent.getId();
    }
}