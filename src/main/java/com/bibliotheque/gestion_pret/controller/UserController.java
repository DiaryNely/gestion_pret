package com.bibliotheque.gestion_pret.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Reservation;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.TypePretRepository;
import com.bibliotheque.gestion_pret.service.LivreService;
import com.bibliotheque.gestion_pret.service.PretService;
import com.bibliotheque.gestion_pret.service.ProlongationService;
import com.bibliotheque.gestion_pret.service.ReservationService;
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

    @Autowired
    private TypePretRepository typePretRepository;

    @Autowired
    private LivreService livreService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/dashboard/{id}")
    public String userDashboard(@PathVariable Long id,
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        List<Livre> livres = userService.searchLivres(query);

        for (Livre livre : livres) {
            int disponibles = livreService.calculerNbExemplairesDisponibles(livre);
            livre.setNbExemplairesDisponibles(disponibles);
        }

        model.addAttribute("livres", livres);
        model.addAttribute("userId", id);
        model.addAttribute("typesDePret", typePretRepository.findAll());

        model.addAttribute("searchQuery", query);

        return "user/user-dashboard";
    }

    @PostMapping("/emprunter")
    public String emprunterLivre(@RequestParam("livreId") Long livreId, @RequestParam("typePretId") Long typePretId,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé dans la session"));

        try {
            pretService.emprunterLivre(adherent.getId(), livreId, typePretId);
            redirectAttributes.addFlashAttribute("successMessage", "Livre emprunté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/dashboard/" + adherent.getId();
    }

    @GetMapping("/mes-prets")
    public String mesPrets(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé dans la session"));

        List<Pret> prets = userService.getPretsByAdherentId(adherent.getId());

        model.addAttribute("prets", prets);
        model.addAttribute("userId", adherent.getId());

        return "user/mes-prets";
    }

    @PostMapping("/rendre")
    public String rendreLivre(@RequestParam("pretId") Long pretId,
            @RequestParam("dateRetour") LocalDate dateRetour,
            RedirectAttributes redirectAttributes) {

        try {
            pretService.rendreLivre(pretId, dateRetour);
            redirectAttributes.addFlashAttribute("successMessage", "Livre retourné avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du retour du livre : " + e.getMessage());
        }

        return "redirect:/user/mes-prets";
    }

    @Autowired
    private ProlongationService prolongationService;

    @PostMapping("/prolonger")
    public String demanderProlongation(@RequestParam("pretId") Long pretId,
            @RequestParam("motif") String motif,
            RedirectAttributes redirectAttributes) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Adherent demandeur = adherentRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

            prolongationService.demanderProlongation(pretId, demandeur, motif);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Votre demande de prolongation a bien été envoyée. Elle est en attente de validation.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/user/mes-prets";
    }

    @PostMapping("/reserver")
    public String reserverLivre(@RequestParam("livreId") Long livreId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Adherent adherent = adherentRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

        try {
            reservationService.creerReservation(adherent.getId(), livreId);
            redirectAttributes.addFlashAttribute("successMessage", "Votre réservation a été enregistrée !");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/dashboard/" + adherent.getId();
    }

    @GetMapping("/mes-reservations")
    public String mesReservations(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé dans la session"));

        List<Reservation> reservations = reservationService.listerReservationsParAdherent(adherent.getId());

        model.addAttribute("typesDePret", typePretRepository.findAll());

        model.addAttribute("reservations", reservations);
        model.addAttribute("userId", adherent.getId());

        return "user/mes-reservations";
    }

    @PostMapping("/reservations/annuler")
    public String annulerReservation(@RequestParam("reservationId") Long reservationId,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

        try {
            reservationService.annulerReservation(reservationId, adherent.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Votre réservation a bien été annulée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'annulation : " + e.getMessage());
        }

        return "redirect:/user/mes-reservations";
    }

    @PostMapping("/reservations/emprunter")
    public String emprunterLivreReserve(@RequestParam("reservationId") Long reservationId,
            @RequestParam("typePretId") Long typePretId,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Adherent adherent = adherentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

        try {
            pretService.emprunterLivreReserve(reservationId, adherent.getId(), typePretId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Livre emprunté avec succès depuis votre réservation !");
            return "redirect:/user/mes-prets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'emprunt : " + e.getMessage());
            return "redirect:/user/mes-reservations";
        }
    }
}