package com.bibliotheque.gestion_pret.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdherentRepository adherentRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // On récupère toutes les listes et statistiques d'un coup
        model.addAttribute("stats", adminService.getDashboardStatistics());
        model.addAttribute("livres", adminService.getAllLivres());
        model.addAttribute("adherents", adminService.getAllAdherents());

        return "admin/admin-dashboard";
    }

    @GetMapping("/prets")
    public String gestionPrets(Model model) {
        List<Pret> prets = adminService.getAllPrets();
        model.addAttribute("prets", prets);
        return "admin/admin-prets";
    }

    @GetMapping("/demandes")
    public String voirDemandes(Model model) {
        model.addAttribute("demandes", adminService.getDemandesEnAttente());
        return "admin/admin-demandes"; // Le nom du nouveau fichier HTML
    }

    @PostMapping("/demandes/traiter")
    public String traiterDemande(@RequestParam Long prolongationId, @RequestParam String action,
            RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'ID de l'admin connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            Adherent admin = adherentRepository.findByEmail(adminEmail).get();

            adminService.traiterDemandeProlongation(prolongationId, action, admin.getId());
            redirectAttributes.addFlashAttribute("successMessage", "La demande a été traitée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/demandes";
    }
}
