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

import com.bibliotheque.gestion_pret.enums.StatutPaiementPenalite;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Penalite;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.service.AdherentService;
import com.bibliotheque.gestion_pret.service.AdminService;
import com.bibliotheque.gestion_pret.service.PenaliteService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private PenaliteService penaliteService;

    @Autowired
    private AdherentService adherentService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
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
        return "admin/admin-demandes";
    }

    @PostMapping("/demandes/traiter")
    public String traiterDemande(@RequestParam Long prolongationId, @RequestParam String action,
            RedirectAttributes redirectAttributes) {
        try {
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

    @GetMapping("/penalites")
    public String gestionPenalites(Model model) {
        List<Penalite> penalitesImpayees = penaliteService.listerPenalitesParStatut(StatutPaiementPenalite.impaye);

        model.addAttribute("penalites", penalitesImpayees);
        model.addAttribute("statuts", StatutPaiementPenalite.values());
        return "admin/gestion-penalites";
    }

    @PostMapping("/penalites/maj-statut")
    public String mettreAJourStatutPenalite(@RequestParam("penaliteId") Long penaliteId,
            @RequestParam("statut") StatutPaiementPenalite nouveauStatut,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {
        try {
            penaliteService.changerStatutPenalite(penaliteId, nouveauStatut, notes);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Le statut de la pénalité a été mis à jour avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/penalites";
    }

    @GetMapping("/adherents")
    public String gestionAdherents(Model model) {
        model.addAttribute("adherents", adherentRepository.findAll());
        return "admin/gestion-adherents";
    }

    @PostMapping("/adherents/lever-suspension")
    public String leverSuspensionAdherent(@RequestParam("adherentId") Long adherentId,
            RedirectAttributes redirectAttributes) {
        try {
            adherentService.leverSuspension(adherentId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "La suspension de l'adhérent a été levée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/adherents";
    }
}
